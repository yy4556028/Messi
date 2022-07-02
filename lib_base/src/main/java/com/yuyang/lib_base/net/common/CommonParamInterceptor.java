package com.yuyang.lib_base.net.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class CommonParamInterceptor implements Interceptor {

    private Map<String, String> headerParamsMap;

    private Map<String, String> paramsMap;

    public CommonParamInterceptor(Map<String, String> headerParamsMap, Map<String, String> paramsMap) {
        this.headerParamsMap = headerParamsMap;
        this.paramsMap = paramsMap;
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (headerParamsMap != null && headerParamsMap.size() > 0) {
            request = getCommonHeaderRequest(request);
        }

        if (paramsMap != null && paramsMap.size() > 0) {
            request = getCommonParamsRequest(request, paramsMap);
        }

        return chain.proceed(request);
    }

    private Request getCommonHeaderRequest(Request request) {
        Headers.Builder headerBuilder = request.headers().newBuilder();
        if (headerParamsMap.size() > 0) {
            for (Entry<String, String> stringStringEntry : headerParamsMap.entrySet()) {
                Entry entry = (Entry) stringStringEntry;
                headerBuilder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }

//        if (headerLinesList.size() > 0) {
//            for (String line : headerLinesList) {
//                headerBuilder.add(line);
//            }
//        }
        return request.newBuilder().headers(headerBuilder.build()).build();
    }

    private Request getCommonParamsRequest(Request request, Map<String, String> params) {
        String method = request.method();
        if ("GET".equals(method)) {
            Builder builder = request.url().newBuilder();
            Iterator var9 = params.entrySet().iterator();

            while (var9.hasNext()) {
                Entry<String, String> entry = (Entry) var9.next();
                builder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }

            HttpUrl modifiedUrl = builder.build();
            return request.newBuilder().url(modifiedUrl).build();
        } else {
            if ("POST".equals(method)) {
                RequestBody body = request.body();
                if (body != null) {
                    RequestBody newBody = null;
                    Iterator var6;
                    Entry entry;
                    int i;
                    if (body instanceof FormBody) {
                        okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
                        var6 = params.entrySet().iterator();

                        while (var6.hasNext()) {
                            entry = (Entry) var6.next();
                            builder.add((String) entry.getKey(), (String) entry.getValue());
                        }

                        FormBody formBody = (FormBody) body;

                        for (i = 0; i < formBody.size(); ++i) {
                            builder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                        }

                        newBody = builder.build();
                    } else if (body instanceof MultipartBody) {
                        okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder();
                        builder.setType(MultipartBody.FORM);
                        var6 = params.entrySet().iterator();

                        while (var6.hasNext()) {
                            entry = (Entry) var6.next();
                            builder.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
                        }

                        MultipartBody multipartBody = (MultipartBody) body;

                        for (i = 0; i < multipartBody.size(); ++i) {
                            builder.addPart(multipartBody.part(i));
                        }

                        newBody = builder.build();
                    }

                    if (null != newBody) {
                        return request.newBuilder().url(request.url()).method(request.method(), (RequestBody) newBody).build();
                    }
                }
            }

            return request;
        }
    }
}

