package com.hanvon.canvasdemo.Deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hanvon.canvasdemo.beans.Template;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class TemplateDeserializer implements JsonDeserializer<Template> {
    @Override
    public Template deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final int width = jsonObject.get("width").getAsInt();
        final int height = jsonObject.get("height").getAsInt();
//        final float x = jsonObject.get("x").getAsFloat();
//        final float y = jsonObject.get("y").getAsFloat();

        final JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
        final int[] data = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++){
            data[i] = jsonArray.get(i).getAsInt();
        }

        final Template template = new Template();
        template.setWidth(width);
        template.setHeight(height);
//        template.setX(x);
//        template.setY(y);
        template.setData(data);

        return null;
    }
}
