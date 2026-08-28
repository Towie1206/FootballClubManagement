package com.eaut.footballclubmanagement.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class RetrofitClient {
    // Nếu chạy máy ảo Android (Emulator), dùng 10.0.2.2 thay cho localhost.
    // Nếu chạy máy thật, đổi thành IP của máy tính (VD: 192.168.1.x)
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // TẠO CƠ CHẾ BẢO MẬT: Tự động bơm API Key vào mọi Request
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("x-api-key", "phui-secret-2026")
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
