package com.hanvon.canvasdemo.Deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hanvon.canvasdemo.beans.Point;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final int strokeID = jsonObject.get("strokeID").getAsInt();
        final float x = jsonObject.get("x").getAsFloat();
        final float y = jsonObject.get("y").getAsFloat();
        final int pressure = jsonObject.get("pressure").getAsInt();

        final Point point = new Point();
        point.setStrokeID(strokeID);
        point.setX(x);
        point.setY(y);
        point.setPressure(pressure);
        return point;
    }
}
