package com.hanvon.canvasdemo.Deserializer;

import android.graphics.Rect;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hanvon.canvasdemo.beans.Pen;
import com.hanvon.canvasdemo.beans.Point;
import com.hanvon.canvasdemo.beans.Stroke;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by pc on 2017/11/16.
 */

public class StrokeDeserializer implements JsonDeserializer<Stroke> {
    @Override
    public Stroke deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Stroke stroke = new Stroke();
        final JsonObject jsonObject = json.getAsJsonObject();

        final int strokeID = jsonObject.get("strokeID").getAsInt();
        final int currentLayer = jsonObject.get("currentLayer").getAsInt();
        final boolean isVisible = jsonObject.get("isVisible").getAsBoolean();

        final Rect rect_big = context.deserialize(jsonObject.get("rect_big").getAsJsonObject(), Rect.class);
        final Pen pen = context.deserialize(jsonObject.get("pen").getAsJsonObject(), Pen.class);

        stroke.setStrokeID(strokeID);
        stroke.setCurrentLayer(currentLayer);
        stroke.setVisible(isVisible);
        stroke.setRect_big(rect_big);
        stroke.setPen(pen);

        JsonElement pointArray = jsonObject.get("points");
        if (pointArray != null) {
            LinkedList<Point> points = new LinkedList<>();
            if (pointArray.isJsonArray()) {
                //类型正确
                JsonArray jsonArray = pointArray.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jo = jsonArray.get(i).getAsJsonObject();
                    Point point = context.deserialize(jo, Point.class);
                    points.add(point);
                }
            } else {
//                //类型错误
//                String value = pointArray.getAsString();
//                Point[] a = new Gson().fromJson(value, Point[].class);
//                points = (LinkedList<Point>) Arrays.asList(a);
            }
            stroke.setPoints(points);
        }

        JsonElement point_partArray = jsonObject.get("points_part");
        if (point_partArray != null) {
            LinkedList<Point> points = new LinkedList<>();
            if (point_partArray.isJsonArray()) {
                //类型正确
                JsonArray jsonArray = point_partArray.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jo = jsonArray.get(i).getAsJsonObject();
                    Point point = context.deserialize(jo, Point.class);
                    points.add(point);
                }
            } else {
//                //类型错误
//                String value = point_partArray.getAsString();
//                Point[] a = new Gson().fromJson(value, Point[].class);
//                points = (LinkedList<Point>) Arrays.asList(a);
            }
            stroke.setPoints_part(points);
        }

        JsonElement rectArray = jsonObject.get("rects");
        if (rectArray != null) {
            LinkedList<Rect> rects = new LinkedList<>();
            if (rectArray.isJsonArray()) {
                //类型正确
                JsonArray jsonArray = rectArray.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jo = jsonArray.get(i).getAsJsonObject();
                    Rect rect = context.deserialize(jo, Rect.class);
                    rects.add(rect);
                }
            } else {
//                //类型错误
//                String value = rectArray.getAsString();
//                Rect[] a = new Gson().fromJson(value, Rect[].class);
//                rects = (LinkedList<Rect>) Arrays.asList(a);
            }
            stroke.setRects(rects);
        }
        return stroke;
    }
}
