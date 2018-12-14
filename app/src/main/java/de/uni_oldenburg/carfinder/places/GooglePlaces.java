package de.uni_oldenburg.carfinder.places;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GooglePlaces {
    private static  GooglePlaces instance;
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private GooglePlacesAPI api;

    public static GooglePlaces getInstance() {
        if (instance == null)
            instance = new GooglePlaces();
        return instance;
    }

    private GooglePlaces() {
        this.init();
    }

    private void init() {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", "AIzaSyD3T_RlOwz_oWjdH4LtQHMdKfRT8Nq_oCA") //TODO: HIDEME
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(httpClient.build())
                .build();
        api = retrofit.create(GooglePlacesAPI.class);

    }

    public void getNearbyParkingPlaces(double lat, double lon, Callback<PlacesResult> cb) {
        Call<PlacesResult> call = api.getNearbyParkingSpots(lat + "," + lon, 3000, "parking");
        call.enqueue(cb);
    }
}
