package com.hanvon.canvasdemo.Deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hanvon.canvasdemo.beans.Pen;
import com.hanvon.canvasdemo.beans.Template;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class PenDeserializer implements JsonDeserializer<Pen> {
    @Override
    public Pen deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final int type = jsonObject.get("type").getAsInt();
        final int width = jsonObject.get("width").getAsInt();
        final int color = jsonObject.get("color").getAsInt();
        final boolean isBeautify = jsonObject.get("isBeautify").getAsBoolean();
        final boolean isTransparent= jsonObject.get("isTransparent").getAsBoolean();
        final int alpha = jsonObject.get("alpha").getAsInt();
        final Template template = context.deserialize(jsonObject.get("template").getAsJsonObject(), Template.class);

        final Pen pen = new Pen();
        pen.setType(type);
        pen.setWidth(width);
        pen.setColor(color);
        pen.setBeautify(isBeautify);
        pen.setTransparent(isTransparent);
        pen.setAlpha(alpha);
        pen.setTemplate(template);

        return null;
    }
}
