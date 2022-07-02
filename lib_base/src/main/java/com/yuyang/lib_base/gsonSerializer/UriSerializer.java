package com.yuyang.lib_base.gsonSerializer;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UriSerializer implements JsonSerializer<Uri>, JsonDeserializer<Uri> {

    // 反序列化器
    @Override
    public JsonElement serialize(Uri src, Type arg1, JsonSerializationContext arg2) {
        return new JsonPrimitive(src.toString());
    }

    // 序列化器
    @Override
    public Uri deserialize(JsonElement src, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(src.getAsString());
    }

}