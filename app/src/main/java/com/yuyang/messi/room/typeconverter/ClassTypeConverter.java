package com.yuyang.messi.room.typeconverter;

import android.app.Activity;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yuyang.lib_base.gsonSerializer.ClassSerializer;

import java.lang.reflect.Type;

public class ClassTypeConverter {

    @TypeConverter
    public String classToString(Class<? extends Activity> clazz) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassSerializer()).create();
        return gson.toJson(clazz);
    }

    @TypeConverter
    public Class<? extends Activity> stringToClass(String json) {
        Type listType = new TypeToken<Class<? extends Activity>>(){}.getType();
//        JSON.parseObject(json, Class.class);
        return new Gson().fromJson(json, listType);
    }
}
