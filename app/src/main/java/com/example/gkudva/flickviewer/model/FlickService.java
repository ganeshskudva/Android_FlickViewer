package com.example.gkudva.flickviewer.model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by gkudva on 14/09/17.
 */

public interface FlickService {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed")
    Observable<Response> getResponse(@Query("page") String page);

    @GET("movie/{id}/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed")
    Observable<VideoResponse> getVideoResponse(@Path("id") String id);

    class Factory {
        public static FlickService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            return retrofit.create(FlickService.class);
        }
    }

}

