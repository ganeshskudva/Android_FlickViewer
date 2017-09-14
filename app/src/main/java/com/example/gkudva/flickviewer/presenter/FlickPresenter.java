package com.example.gkudva.flickviewer.presenter;

import android.util.Log;

import com.example.gkudva.flickviewer.model.Flick;
import com.example.gkudva.flickviewer.model.FlickService;
import com.example.gkudva.flickviewer.model.FlickVideo;
import com.example.gkudva.flickviewer.model.VideoResponse;
import com.example.gkudva.flickviewer.view.FlickMvpView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gkudva on 14/09/17.
 */

public class FlickPresenter implements Presenter<FlickMvpView> {

    private FlickMvpView flickMvpView;
    private Flick movie;
    private Subscription subscription;
    private FlickService flickService;
    private List<FlickVideo> flickVideoList;
    private static final String TAG = "FlicksPresenter";

    private YouTubePlayer YPlayer;
    private boolean fullScreen;

    @Override
    public void attachView(FlickMvpView view) {
        this.flickMvpView = view;
    }

    @Override
    public void detachView() {
        this.flickMvpView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void showData(Flick flick) {

        movie = flick;

        // Check if need to show video
        checkIfVideo();
    }

    private void checkIfVideo() {

        if (flickService == null)
            flickService = FlickService.Factory.create();

        subscription = flickService.getVideoResponse(Integer.toString(movie.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<VideoResponse>() {
                    @Override
                    public void onCompleted() {
                        if (!flickVideoList.isEmpty()) {
                            getMovieVideo();
                        } else {
                            flickMvpView.showMessage("Video Trailer not found");
                        }

                        flickMvpView.showMovieData();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading flicks ", error);
                        if (isHttp404(error)) {
                            flickMvpView.showMessage("HTTP Error");
                        } else {
                            flickMvpView.showMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(VideoResponse response) {
                        FlickPresenter.this.flickVideoList = response.getFlickVideos();
                    }
                });

    }

    private void getMovieVideo() {

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize("AIzaSyAsG6oMoSX4w-IhjaeVrBxIieniRVdzQqI", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    YPlayer = player;

                    ArrayList<String> keys = new ArrayList<>();
                    for (FlickVideo video : flickVideoList) {
                        if ("YouTube".equals(video.getSite())) keys.add(video.getKey());
                    }

                    if (movie.getVoteAverage().compareTo(5D) > 0) {
                        // Play videos automatically
                        YPlayer.loadVideos(keys);
                    } else {
                        // Load videos but do not play automatically
                        YPlayer.cueVideos(keys);
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

            }
        });
        flickMvpView.showVideo(youTubePlayerFragment);
    }


    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }
}

