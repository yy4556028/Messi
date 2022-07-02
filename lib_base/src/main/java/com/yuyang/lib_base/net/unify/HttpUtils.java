package com.yuyang.lib_base.net.unify;


import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yuyang.lib_base.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class HttpUtils {

    private final int READ_SIZE = 4 * 1024;

    private static OkHttpClient okHttpClient = OKHttpUtil.getClientInstance();
    private static HttpResultHandler httpResultHandler = new HttpResultHandler();

    public HttpUtils() {
    }

    /**
     * form类型 表单类型数据请求
     *
     * @param method
     * @param url
     * @param callBack
     * @param <T>
     * @return
     */
    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestCallBack<T> callBack) {
        return this.send(method, url, null, callBack);
    }

    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestParams params, RequestCallBack<T> callBack) {
        if (url == null) {
            throw new IllegalArgumentException("url may not be null");
        }  else if (method == HttpRequest.HttpMethod.POST) {
            return new HttpHandler(post(url, params, callBack, null));
        } else if (method == HttpRequest.HttpMethod.GET) {
            return new HttpHandler(get(url, params, callBack, null));
        } else {
            throw new IllegalArgumentException("method not found");
        }
    }

    /**
     * 文件下载
     *
     * @param url
     * @param target
     * @param params
     * @param callback
     * @return
     */
    public HttpHandler<File> download(String url, String target, RequestParams params, RequestCallBack<File> callback) {
        return new HttpHandler(post(url, params, callback, target));
    }

    private Call post(String url, final RequestParams params, final RequestCallBack callBack, final String filePath) {

        Request.Builder builder = new Request.Builder().url(url);

        String par = "?";
        //为了兼容文件类型上传 选择使用MultipartBuilder 只有form类型可以选择使用FormEncodingBuilder
        FormBody.Builder formEncodingBuilder = null;
        MultipartBody.Builder multipartBuilder = null;

        if (params != null && params.size() > 0) {

            if (params.isIncludeFile) {
                multipartBuilder = new MultipartBody.Builder();
            } else {
                formEncodingBuilder = new FormBody.Builder();
            }

            Iterator iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = params.get(key);
                if (value instanceof String) {
                    //Log.d("abc", key.toString() + " = " + value.toString());
                    par = par + key.toString() + "=" + value.toString() + "&";
                    if (params.isIncludeFile) {
                        multipartBuilder.addFormDataPart(key.toString(), value.toString());
                    } else {
                        formEncodingBuilder.add(key.toString(), value.toString());
                    }
                } else if (value instanceof File) {
                    par = par + key.toString() + "=" + ((File) value).getName() + "&";
                    multipartBuilder.addFormDataPart(key.toString(), ((File) value).getName(), RequestBody.create(null, (File) value));
                }
            }

            if (params.isIncludeFile) {
                builder.post(multipartBuilder.build());
            } else {
                builder.post(formEncodingBuilder.build());
            }
        } else {
            builder.post(new FormBody.Builder().build());
        }

        final String finalUrl = url + par.substring(0, par.length() - 1);
        LogUtil.e("Unify-Http-Request", finalUrl);

//        builder.removeHeader("User-Agent");
//        builder.addHeader("User-Agent", CookieJarManager.getUserAgent());
//        builder.addHeader("csrf_token", gettCookieCsrf());
//        builder.addHeader("Request-Time", String.valueOf(System.currentTimeMillis()));
//        builder.addHeader("session", UserConstant.TOKEN);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.e("Unify-Http-onFailure", finalUrl + "\r\n" + e.getMessage());

                MessageExtra messageExtra = new MessageExtra();
                messageExtra.callBack = callBack;
                messageExtra.exception = e;
                Message message = Message.obtain();
                message.what = HttpResultHandler.MSG_CODE_FAIL;
                message.obj = messageExtra;
                httpResultHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                MessageExtra messageExtra = new MessageExtra();
                messageExtra.callBack = callBack;

                if (filePath != null) {
                    InputStream inputStream = response.body().byteStream();
                    File file = new File(filePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    OutputStream outputStream = new FileOutputStream(file);
                    long max = response.body().contentLength();
                    int curr = 0;
                    int readCount;
                    int loopCount = 0;
                    byte[] byteBuf = new byte[READ_SIZE];
                    while ((readCount = inputStream.read(byteBuf, 0, READ_SIZE)) > 0) {
                        curr += readCount;
                        loopCount++;
                        outputStream.write(byteBuf, 0, readCount);
                        if (loopCount % 25 == 0) {
                            messageExtra.max = max;
                            messageExtra.curr = curr;
                            Message msg = Message.obtain();
                            msg.what = HttpResultHandler.MSG_CODE_LOADING;
                            msg.obj = messageExtra;
                            httpResultHandler.sendMessage(msg);
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                }
                if (filePath != null) {
                    messageExtra.content = new File(filePath);
                } else {
                    messageExtra.content = response.body().string();
                }

                String responseStr = finalUrl + "\r\n result = " + messageExtra.content;

                LogUtil.v("Unify-Http-onResponse", responseStr);

                Message message = Message.obtain();
                message.what = HttpResultHandler.MSG_CODE_SUCCESS;
                message.obj = messageExtra;
                httpResultHandler.sendMessage(message);
            }
        });

        return call;
    }

    private Call get(String url, final RequestParams params, final RequestCallBack callBack, final String filePath) {
        if (params != null && params.size() > 0) {
            Iterator iterator = params.keySet().iterator();
            StringBuilder urlBuilder = new StringBuilder(url);
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = params.get(key);
                if (urlBuilder.indexOf("?") == -1) {
                    urlBuilder.append("?").append(key.toString()).append("=").append(value.toString());
                } else {
                    urlBuilder.append("&").append(key.toString()).append("=").append(value.toString());
                }
            }
            url = urlBuilder.toString();
        }
        Request.Builder builder = new Request.Builder().url(url);
        builder.get();
        final String finalUrl = url;
        LogUtil.e("Unify-Http-Request", finalUrl);

        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("Unify-Http-onFailure", finalUrl + "\r\n" + e.getMessage());

                MessageExtra messageExtra = new MessageExtra();
                messageExtra.callBack = callBack;
                messageExtra.exception = e;
                Message message = Message.obtain();
                message.what = HttpResultHandler.MSG_CODE_FAIL;
                message.obj = messageExtra;
                httpResultHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MessageExtra messageExtra = new MessageExtra();
                messageExtra.callBack = callBack;

                if (filePath != null) {
                    InputStream inputStream = response.body().byteStream();
                    File file = new File(filePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    OutputStream outputStream = new FileOutputStream(file);
                    long max = response.body().contentLength();
                    int curr = 0;
                    int readCount;
                    int loopCount = 0;
                    byte[] byteBuf = new byte[READ_SIZE];
                    while ((readCount = inputStream.read(byteBuf, 0, READ_SIZE)) > 0) {
                        curr += readCount;
                        loopCount++;
                        outputStream.write(byteBuf, 0, readCount);
                        if (loopCount % 25 == 0) {
                            messageExtra.max = max;
                            messageExtra.curr = curr;
                            Message msg = Message.obtain();
                            msg.what = HttpResultHandler.MSG_CODE_LOADING;
                            msg.obj = messageExtra;
                            httpResultHandler.sendMessage(msg);
                        }
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                }
                if (filePath != null) {
                    messageExtra.content = new File(filePath);
                } else {
                    messageExtra.content = response.body().string();
                }

                String responseStr = finalUrl + "\r\n result = " + messageExtra.content;

                LogUtil.v("Unify-Http-onResponse", responseStr);

                Message message = Message.obtain();
                message.what = HttpResultHandler.MSG_CODE_SUCCESS;
                message.obj = messageExtra;
                httpResultHandler.sendMessage(message);
            }
        });

        return call;
    }

    public static String bodyToString(final okhttp3.Request request) {
        if (!"POST".equals(request.method())) {
            return "";
        }
        try {
            okhttp3.Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "something error when show requestBody.";
        }
    }

    public static String getResponseBody(Response response) {

        Charset UTF8 = Charset.forName("UTF-8");
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }
        return buffer.clone().readString(charset);
    }

    private static class HttpResultHandler extends Handler {
        static final int MSG_CODE_SUCCESS = 0x1;
        static final int MSG_CODE_FAIL = 0x2;
        static final int MSG_CODE_LOADING = 0x3;

        @Override
        public void handleMessage(Message msg) {
            MessageExtra extra = null;
            switch (msg.what) {
                case MSG_CODE_SUCCESS:
                    extra = (MessageExtra) msg.obj;
                    ResponseInfo responseInfo = new ResponseInfo(extra.content);
                    responseInfo.max = extra.max;
                    extra.callBack.onSuccess(responseInfo);
                    break;
                case MSG_CODE_FAIL:
                    extra = (MessageExtra) msg.obj;
                    extra.callBack.onFailure(new HttpException(extra.exception.getMessage()), "");
                    break;
                case MSG_CODE_LOADING:
                    extra = (MessageExtra) msg.obj;
                    extra.callBack.onLoading(extra.max, extra.curr, false);
                    break;
            }
        }
    }
}
