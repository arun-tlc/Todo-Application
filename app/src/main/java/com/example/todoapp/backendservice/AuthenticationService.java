package com.example.todoapp.backendservice;

import androidx.annotation.NonNull;

import com.example.todoapp.model.Credential;
import com.example.todoapp.model.ResetPasswordRequest;
import com.example.todoapp.model.SignUpRequest;
import com.example.todoapp.model.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

        executeRequest(call, callBack);
    }

    public void resetPassword(final Credential credential, final String newHint,
                              final ApiResponseCallBack callBack) {
        final ResetPasswordRequest resetPassword = new ResetPasswordRequest(credential, newHint);
        final Call<ResponseBody> call = apiService.resetPassword(resetPassword);

        executeRequest(call, callBack);
    }

    public void login(final Credential credential, final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.login(credential);

        executeRequest(call, callBack);
    }

    private void executeRequest(final Call<ResponseBody> call, final ApiResponseCallBack callBack) {
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

    public interface ApiResponseCallBack {

        void onSuccess(final String responseBody);
        void onError(final String errorMessage);
    }
}
