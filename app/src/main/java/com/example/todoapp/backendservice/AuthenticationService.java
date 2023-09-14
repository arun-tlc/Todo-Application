package com.example.todoapp.backendservice;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.todoapp.model.Credential;
import com.example.todoapp.model.ResetPasswordRequest;
import com.example.todoapp.model.SignUpRequest;
import com.example.todoapp.model.UserProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationService {

    private final ApiService apiService;

    public AuthenticationService (final String baseUrl) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void signUp(final UserProfile userProfile, final Credential signUpDetail,
                          final ApiResponseCallBack callBack) {
        final SignUpRequest signUpRequest = new SignUpRequest(userProfile, signUpDetail);
        final Call<ResponseBody> call = apiService.signUp(signUpRequest);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call,
                                   @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    callBack.onSuccess(response.body().toString());
                } else {
                    callBack.onError(String.format("Request failed with status code %d", response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onError(t.getMessage());
            }
        });
    }

    public void resetPassword(final Credential credential, final String newHint,
                              final ApiResponseCallBack callBack) {
        final ResetPasswordRequest resetPassword = new ResetPasswordRequest(credential, newHint);
//        final String requestBody = String.format("email : %s, password : %s, oldHint : %s, newHint : %s",
//                credential.getEmail(), credential.getPassword(), credential.getHint(), newHint);
        final Call<ResponseBody> call = apiService.resetPassword(resetPassword);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    callBack.onSuccess(response.body().toString());
                } else {
                    callBack.onError(String.format("Request failed with status code %s", response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onError(t.getMessage());
            }
        });
    }

    public void login(final Credential credential, final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.login(credential);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    callBack.onSuccess(response.body().toString());
                } else {
                    callBack.onError(String.format("Request failed with status code %s", response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callBack.onError(t.getMessage());
            }
        });
    }

    public interface ApiResponseCallBack {

        void onSuccess(final String responseBody);
        void onError(final String errorMessage);
    }
}
