package com.example.gkudva.flickviewer.view;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * Created by gkudva on 14/09/17.
 */

public interface FlickMvpView extends MvpView{

    void showMovieData();
    void showVideo(YouTubePlayerSupportFragment youTubePlayerFragment);
    void showMessage(String message);
}
