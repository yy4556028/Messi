package com.yuyang.lib_base.net.common.convert;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.yuyang.lib_base.BuildConfig;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 自定义Gson转换器
 * Created by 17112281 on 2018/11/19.
 */
public class CustomizeGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public CustomizeGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        if (BuildConfig.DEBUG) {
            Log.d("rfhost", "原始返回数据：" + response);
        }

        IResponse baseResponse = null;
        try {
            /**
             * {"version":"19.2.4.6","downloadAddr":"http"}
             *  TODO ? 这种格式为毛不报异常
             */
            baseResponse = gson.fromJson(response, IResponse.class);
        } catch (JsonParseException e) {
            //Log.e("rfhost", "转换异常");
        }
        if (baseResponse == null) {
            return adapter.fromJson(response);
        } else {
            if (baseResponse.getStatus() == -100) { // 临时方案 针对如上json格式
                return adapter.fromJson(response);
            } else if (baseResponse.getStatus() != 0) {
                value.close();
                throw new RuntimeException(baseResponse.getMessage() + "" + baseResponse.getStatus(), null);
            }
            try {
                IResponse.JsonDataBean jsonData = baseResponse.getJsonData();
                String s = gson.toJson(jsonData);
                if (s == null || TextUtils.equals("null", s)) {
                    s = "0";
                }
                if (BuildConfig.DEBUG) {
                    Log.e("rfhost", "处理后的返回数据" + s);
                }
                return adapter.fromJson(s);
            } finally {
                value.close();
            }
        }
    }
}
