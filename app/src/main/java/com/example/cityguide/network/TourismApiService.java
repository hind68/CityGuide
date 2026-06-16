package com.example.cityguide.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TourismApiService {
    @GET("places")
    Call<List<RemotePlaceDto>> getPlaces();

    @GET("suggestions")
    Call<List<RemotePlaceDto>> getSuggestions();
}
