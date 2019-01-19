package de.uni_oldenburg.carfinder.web.ors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenRouteService {
    private static OpenRouteService instance;
    private final static String BASE_URL = "https://api.openrouteservice.org/";
    private OpenRouteServiceAPI api;

    public static OpenRouteService getInstance() {
        if (instance == null)
            instance = new OpenRouteService();
        return instance;
    }

    private OpenRouteService() {
        this.init();
    }

    private void init() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder().addInterceptor(interceptor);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("profile", "foot-walking")
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
        api = retrofit.create(OpenRouteServiceAPI.class);

    }

    public void getRoute(double startingLat, double startingLon, double endingLat, double endingLon, Callback<ORSResult> cb) {
        Call<ORSResult> call = api.getRoute(startingLon + "," + startingLat + "|" + endingLon + "," + endingLat);
        call.enqueue(cb);
    }
}
