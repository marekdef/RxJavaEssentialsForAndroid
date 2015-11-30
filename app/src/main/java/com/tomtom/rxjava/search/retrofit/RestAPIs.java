package com.tomtom.rxjava.search.retrofit;

import com.squareup.okhttp.OkHttpClient;
import com.tomtom.rxjava.search.mapkit.MapkitResult;
import com.tomtom.rxjava.search.result.QueryResult;

import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by defecins on 18/11/15.
 */
public abstract class RestAPIs {
    private static OkHttpClient httpClient = new OkHttpClient();
    private static String DECARTA_BASE_URL = "http://api.decarta.com/v1/";
    private static String MAPKIT_BASE_URL = "https://api.tomtom.com/lbs/services/geocode/4/";

    public interface DecartaSearch {


        DecartaSearch SERVICE = new Retrofit.Builder().baseUrl(DECARTA_BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build().create(DecartaSearch.class);

        @GET("{key}/search/{query}.json")
        Observable<QueryResult> search(@Path("key") String key, @Path("query") String query);
    }

    public interface MapkitSearch {

        MapkitSearch SERVICE = new Retrofit.Builder().baseUrl(MAPKIT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build().create(MapkitSearch.class);
        @GET("geocode?format=json")
        Observable<MapkitResult> geocode(@Query("key") String key, @Query("query") String query);
    }
}
