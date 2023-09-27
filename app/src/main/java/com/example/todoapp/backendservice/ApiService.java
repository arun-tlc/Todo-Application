package com.example.todoapp.backendservice;

import com.example.todoapp.model.Credential;
import com.example.todoapp.model.ResetPasswordRequest;
import com.example.todoapp.model.SignUpRequest;
import com.example.todoapp.model.UserProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/v1/user/signup")
    Call<ResponseBody> signUp(@Body final SignUpRequest signUpRequest);

    @POST("api/v1/user/reset/password")
    Call<ResponseBody> resetPassword(@Body final ResetPasswordRequest resetPasswordRequest);

    @POST("api/v1/user/login")
    Call<ResponseBody> login(@Body final Credential credential);

    @GET("api/v1/user/details")
    Call<ResponseBody> getUserDetail();

    @PUT("api/v1/user/details")
    Call<ResponseBody> updateUserDetail(@Body final UserProfile userProfile);

    @GET("api/v1/user/system/settings")
    Call<ResponseBody> getSystemSetting();

    @PUT("api/v1/user/system/settings")
    Call<ResponseBody> updateSystemSetting(@Query("font_family") final String font,
                                           @Query("font_size") final Integer size,
                                           @Query("color") final String color);

    @FormUrlEncoded
    @PUT("api/v1/user/system/settings")
    Call<ResponseBody> updateFontFamily(@Field("font_family") final String fontFamily);

    @FormUrlEncoded
    @PUT("api/v1/user/system/settings")
    Call<ResponseBody> updateFontSize(@Field("font_size") final int fontSize);

    @FormUrlEncoded
    @PUT("api/v1/user/system/settings")
    Call<ResponseBody> updateColor(@Field("color") final String color);
}
