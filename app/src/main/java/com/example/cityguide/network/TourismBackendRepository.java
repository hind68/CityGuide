package com.example.cityguide.network;

import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TourismBackendRepository {
    private final TourismApiService service;

    public TourismBackendRepository() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(createMockBackendInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cityguide.mock/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(TourismApiService.class);
    }

    public TourismApiService getService() {
        return service;
    }

    public List<RemotePlaceDto> getMockSuggestions() {
        return Arrays.asList(
                new RemotePlaceDto(1, "Jardin Majorelle", "Marrakech", "Garden", 31.6417, -8.0029),
                new RemotePlaceDto(2, "Hassan II Mosque", "Casablanca", "Monument", 33.6084, -7.6326),
                new RemotePlaceDto(3, "Chefchaouen Medina", "Chefchaouen", "Medina", 35.1688, -5.2636)
        );
    }

    private Interceptor createMockBackendInterceptor() {
        return chain -> {
            String path = chain.request().url().encodedPath();
            String json = "[]";
            if ("/suggestions".equals(path) || "/places".equals(path)) {
                json = "[" +
                        "{\"id\":1,\"name\":\"Jardin Majorelle\",\"city\":\"Marrakech\",\"category\":\"Garden\",\"latitude\":31.6417,\"longitude\":-8.0029}," +
                        "{\"id\":2,\"name\":\"Hassan II Mosque\",\"city\":\"Casablanca\",\"category\":\"Monument\",\"latitude\":33.6084,\"longitude\":-7.6326}," +
                        "{\"id\":3,\"name\":\"Chefchaouen Medina\",\"city\":\"Chefchaouen\",\"category\":\"Medina\",\"latitude\":35.1688,\"longitude\":-5.2636}" +
                        "]";
            }
            return new okhttp3.Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(ResponseBody.create(MediaType.get("application/json"), json))
                    .build();
        };
    }
}
