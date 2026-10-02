package com.eaut.footballclubmanagement.network;

import android.content.Context;

import com.eaut.footballclubmanagement.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class RetrofitClient {
    private static volatile ApiService apiService;
    private static volatile SessionManager sessionManager;

    private RetrofitClient() {
    }

    public static void initialize(Context context) {
        if (sessionManager == null) {
            synchronized (RetrofitClient.class) {
                if (sessionManager == null) {
                    sessionManager = new SessionManager(context);
                }
            }
        }
    }

    public static ApiService getApiService() {
        if (sessionManager == null) {
            throw new IllegalStateException("RetrofitClient must be initialized by the Application");
        }
        if (apiService == null) {
            synchronized (RetrofitClient.class) {
                if (apiService == null) {
                    apiService = createApiService();
                }
            }
        }
        return apiService;
    }

    private static ApiService createApiService() {
            String baseUrl = BuildConfig.API_BASE_URL == null ? "" : BuildConfig.API_BASE_URL.trim();
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                throw new IllegalStateException("FCM_API_BASE_URL must start with http:// or https://");
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder request = chain.request().newBuilder()
                            .header("Accept", "application/json");
                    String token = sessionManager.getToken();
                    if (!token.isEmpty()) {
                        request.header("Authorization", "Bearer " + token);
                    }
                    String apiKey = BuildConfig.API_KEY == null ? "" : BuildConfig.API_KEY.trim();
                    if (!apiKey.isEmpty()) {
                        request.header("x-api-key", apiKey);
                    }
                    Response response = chain.proceed(request.build());
                    String path = chain.request().url().encodedPath();
                    if (response.code() == 401 && !path.startsWith("/api/auth/") && !"demo_offline_token".equals(token)) {
                        sessionManager.clearAuth();
                    }
                    return response;
                }
            }).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(ApiService.class);
    }
}
