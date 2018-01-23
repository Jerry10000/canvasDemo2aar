package com.hanvon.canvasdemo.Deserializer;

import android.graphics.Rect;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by pc on 2017/11/16.
 */

public class RectDeserializer implements JsonDeserializer<Rect> {
    @Override
    public Rect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final int bottom = jsonObject.get("bottom").getAsInt();
        final int left = jsonObject.get("left").getAsInt();
        final int right = jsonObject.get("right").getAsInt();
        final int top = jsonObject.get("top").getAsInt();

        final Rect rect = new Rect();
        rect.set(left, top, right, bottom);

        return null;
    }
}
