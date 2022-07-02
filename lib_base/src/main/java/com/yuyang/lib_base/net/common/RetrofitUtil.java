package com.yuyang.lib_base.net.common;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.yuyang.lib_base.BaseApp;
import com.yuyang.lib_base.BuildConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * https://blog.csdn.net/carson_ho/article/details/73732076
 */
public class RetrofitUtil {

    private static Retrofit retrofit;

    public static Retrofit getInstance(String baseUrl, Map<String, String> params) {

        if (retrofit == null) {

            synchronized (Retrofit.class) {
                if (retrofit == null) {

                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
                    if (BuildConfig.DEBUG) {
                        // development build
                        httpLoggingInterceptor.setLevel(Level.BODY);
                    } else {
                        // production build
                        httpLoggingInterceptor.setLevel(Level.BASIC);
                    }

                    ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(BaseApp.getInstance()));
                    SSLParams sslParams = SSLParams.getSslSocketFactory(null, null, null);
                    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                            .followSslRedirects(true)
//                            .followRedirects(true)
//                            .protocols(Collections.unmodifiableList(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2)))//启用http2.0协议)
                            .cookieJar(cookieJar)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .addInterceptor(new CommonParamInterceptor(null, params))
                            .addInterceptor(httpLoggingInterceptor)
                            .hostnameVerifier(new SSLParams.UnSafeHostnameVerifier())
                            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

                    retrofit = new Retrofit.Builder()
                            .client(builder.build())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 支持RxJava平台
                            .addConverterFactory(GsonConverterFactory.create())// 设置数据解析器
                            .baseUrl(baseUrl)
                            .build();
                }
            }
        }
        return retrofit;
    }
}
