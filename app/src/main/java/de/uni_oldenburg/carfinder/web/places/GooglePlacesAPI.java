package de.uni_oldenburg.carfinder.web.places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesAPI {

    @GET("nearbysearch/json")
    Call<PlacesResult> getNearbyParkingSpots(@Query("location") String location, @Query("radius") int radius, @Query("type") String type);
}
