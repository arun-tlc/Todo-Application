package com.example.todoapp.backendservice;

import com.example.todoapp.model.Credential;
import com.example.todoapp.model.ResetPasswordRequest;
import com.example.todoapp.model.SignUpRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/user/signup")
    Call<ResponseBody> signUp(@Body final SignUpRequest signUpRequest);

    @POST("api/v1/user/reset/password")
    Call<ResponseBody> resetPassword(@Body final ResetPasswordRequest resetPasswordRequest);

    @POST("api/v1/user/login")
    Call<ResponseBody> login(@Body final Credential credential);
}
