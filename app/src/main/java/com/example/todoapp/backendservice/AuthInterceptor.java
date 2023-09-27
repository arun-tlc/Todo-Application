package com.example.todoapp.backendservice;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final String accessToken;

    public AuthInterceptor(final String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        final Request newRequest = originalRequest.newBuilder()
                .header("Authorization", String.format("Bearer %s", accessToken))
                .build();

        return chain.proceed(newRequest);
    }
}
