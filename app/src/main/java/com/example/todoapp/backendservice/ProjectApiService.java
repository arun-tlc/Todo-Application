package com.example.todoapp.backendservice;

import com.example.todoapp.model.Project;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProjectApiService {

    @POST("api/v1/project")
    Call<ResponseBody> create(@Body Project project);
}
