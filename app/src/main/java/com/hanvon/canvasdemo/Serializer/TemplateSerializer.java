package com.hanvon.canvasdemo.Serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hanvon.canvasdemo.beans.Template;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class TemplateSerializer implements JsonSerializer<Template> {
    @Override
    public JsonElement serialize(Template src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("width", src.getWidth());
        jsonObject.addProperty("height", src.getHeight());
//        jsonObject.addProperty("x", src.getX());
//        jsonObject.addProperty("y", src.getY());

        final JsonElement jsonElement = context.serialize(src.getData());
        jsonObject.add("data", jsonElement);

        return jsonObject;
    }
}
