package com.yuyang.lib_base.gsonSerializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yuyang.lib_base.BaseApp;

import java.lang.reflect.Type;

public class ClassSerializer implements JsonSerializer<Class>, JsonDeserializer<Class> {

    // 对象转为Json时调用,实现JsonSerializer<Class>接口
    @Override
    public JsonElement serialize(Class clazz, Type arg1, JsonSerializationContext arg2) {
        return new JsonPrimitive(clazz.getName());
    }

    // json转为对象时调用,实现JsonDeserializer<Class>接口
    @Override
    public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Class clazz = BaseApp.getInstance().getClassLoader().loadClass(json.getAsString());
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}