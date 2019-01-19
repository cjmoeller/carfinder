package de.uni_oldenburg.carfinder.web.ors;

import retrofit2.Call;
import retrofit2.http.GET;

public interface OpenRouteServiceAPI {
    @GET("directions?api_key=5b3ce3597851110001cf62488bed2386bc4d46688cf421909215b916") //little workaround
    Call<ORSResult> getRoute(@retrofit2.http.Query("coordinates") String coordinates);
}
