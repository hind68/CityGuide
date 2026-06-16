package com.example.cityguide.network;

import java.util.Arrays;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TourismBackendRepository {
    private final TourismApiService service;

    public TourismBackendRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mock.cityguide.local/")
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
}
