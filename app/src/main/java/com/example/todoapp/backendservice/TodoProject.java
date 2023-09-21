package com.example.todoapp.backendservice;

import androidx.annotation.NonNull;

import com.example.todoapp.model.Project;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoProject {

    private final ProjectApiService apiService;

    public TodoProject(final String baseUrl, final String accessToken) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new AuthInterceptor(accessToken));
        final OkHttpClient client = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ProjectApiService.class);
    }

    public void create(final Project project,
                       final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.create(project);

        executeRequest(call, callBack);
    }

    public void getAll(final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getAll();

        executeRequest(call, callBack);
    }

    public void delete(final String id, final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.delete(id);

        executeRequest(call, callBack);
    }

    public void updateOrder(final Project project,
                            final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateOrder(project.getId(),
                Math.toIntExact(project.getProjectOrder()));

        executeRequest(call, callBack);
    }

    private void executeRequest(final Call<ResponseBody> call,
                                final AuthenticationService.ApiResponseCallBack callBack) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    try {
                        callBack.onSuccess(response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        assert response.errorBody() != null;
                        final String errorBody = response.errorBody().string();
                        final JSONObject jsonObject = new JSONObject(errorBody);
                        final String message = jsonObject.getString("message");

                        callBack.onError(message);
                    } catch (IOException | JSONException message) {
                        throw new RuntimeException(message);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onError(t.getMessage());
            }
        });
    }
}
