package com.example.gkudva.flickviewer.view.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gkudva.flickviewer.R;
import com.example.gkudva.flickviewer.model.Flick;
import com.example.gkudva.flickviewer.presenter.FlickPresenter;
import com.example.gkudva.flickviewer.util.InfoMessage;
import com.example.gkudva.flickviewer.view.FlickMvpView;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import icepick.Icepick;

/**
 * Created by gkudva on 14/09/17.
 */

public class FlickActivity extends AppCompatActivity implements FlickMvpView {

    public static final String BUNDLE_MOVIE = "BUNDLE_MOVIE";

    @BindView(R.id.iv_poster)
    ImageView ivPoster;

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @BindView(R.id.rating_bar)
    AppCompatRatingBar mRatingBar;

    @BindView(R.id.iv_backdrop)
    ImageView mIvBackdrop;

    @BindView(R.id.tv_vote_count)
    TextView tvVoteCount;

    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;

    @BindView(R.id.tv_description)
    TextView tvDescription;

    private YouTubePlayer YPlayer;
    private boolean fullScreen;
    private boolean previouslyLoaded;

    private ACProgressFlower mLoadingDialog;

    private Flick movie;
    private FlickPresenter presenter;
    private InfoMessage infoMessage;

    public static Intent newIntent(Context context, Flick flick) {
        Intent intent = new Intent(context, FlickActivity.class);
        intent.putExtra(BUNDLE_MOVIE, flick);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new FlickPresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_flick);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        movie = data.getParcelable(BUNDLE_MOVIE);

        infoMessage = new InfoMessage(this);
        initializeUI();

        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState != null) {
            previouslyLoaded = true;
        }

        setTitle(movie.getTitle());
        presenter.showData(movie);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showMessage(String message) {
        infoMessage.showMessage(message);
    }

    @Override
    public void showVideo(YouTubePlayerSupportFragment youTubePlayerFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, youTubePlayerFragment).commit();
        mFragmentContainer.setVisibility(View.VISIBLE);
    }

    public void initializeUI() {
        mFragmentContainer.setVisibility(View.INVISIBLE);
        mRatingBar.setVisibility(View.INVISIBLE);
        mIvBackdrop.setVisibility(View.INVISIBLE);
        tvDescription.setVisibility(View.INVISIBLE);

        mLoadingDialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.RED)
                .text(getResources().getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        mLoadingDialog.show();

/*
        ivPoster.post(new Runnable() {
            @Override
            public void run() {
                int width = ivPoster.getMeasuredWidth();
                int height = ivPoster.getMeasuredHeight();
                Picasso.with(getApplicationContext()).load(movie.getBackdropPath(780))
                        .resize(width, height)
                        .centerCrop()
                        .into(ivPoster);
            }
        });
*/
    }

    @Override
    public void showMovieData() {
        tvDescription.setText(movie.getOverview());
        if (movie.getAdult())
            tvVoteCount.setText("R");
        else
            tvVoteCount.setText("PG");
        tvReleaseDate.setText(movie.getReleaseDate());

        try {
            LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mRatingBar.getContext(), R.color.yellow), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(mRatingBar.getContext(), R.color.ultra_light_gray), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(mRatingBar.getContext(), R.color.ultra_light_gray), PorterDuff.Mode.SRC_ATOP);
        } catch (Exception ex) {
            // TODO: Do something here?
        }
        mRatingBar.setRating(movie.getVoteAverage().floatValue() / 2);
        mRatingBar.setVisibility(View.VISIBLE);

        mIvBackdrop.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    private void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}

