package com.hanvon.canvasdemo.Serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hanvon.canvasdemo.beans.Pen;
import com.hanvon.canvasdemo.beans.Template;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class PenSerializer implements JsonSerializer<Pen> {
    @Override
    public JsonElement serialize(Pen src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("width", src.getWidth());
        jsonObject.addProperty("color", src.getColor());
        jsonObject.addProperty("isBeautify", src.isBeautify());
        jsonObject.addProperty("isTransparent", src.isTransparent());
        jsonObject.addProperty("alpha", src.getAlpha());

        final JsonElement jsonTemplate = context.serialize(src.getTemplate());
        jsonObject.add("Template", jsonTemplate);

        return jsonObject;
    }
}
