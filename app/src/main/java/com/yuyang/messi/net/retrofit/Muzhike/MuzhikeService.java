package com.yuyang.messi.net.retrofit.Muzhike;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MuzhikeService {

    String BASE_URL = "http://app.muzhike.com.cn/";

    @FormUrlEncoded
    @POST("Home/Mine/mobilephone")//获取验证码
    Call<String> getCode(@Field("phone") String phone);
//    Call<List<String>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);

    @HTTP(method = "GET", path = "blog/{id}", hasBody = false)
    Call<ResponseBody> getCall(@Path("id") int id);
}
