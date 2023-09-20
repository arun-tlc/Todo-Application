package com.example.todoapp.backendservice;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ItemApiService {

    @GET("api/v1/item")
    Call<ResponseBody> getAll();
}
