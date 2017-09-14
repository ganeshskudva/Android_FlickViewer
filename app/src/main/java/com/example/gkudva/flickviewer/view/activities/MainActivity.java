package com.example.gkudva.flickviewer.view.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gkudva.flickviewer.R;
import com.example.gkudva.flickviewer.model.Flick;
import com.example.gkudva.flickviewer.presenter.MainPresenter;
import com.example.gkudva.flickviewer.util.EndlessRecyclerViewScrollListener;
import com.example.gkudva.flickviewer.util.InfoMessage;
import com.example.gkudva.flickviewer.view.MainMvpView;
import com.example.gkudva.flickviewer.view.adapter.FlickAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import icepick.Icepick;

public class MainActivity extends AppCompatActivity implements MainMvpView,SwipeRefreshLayout.OnRefreshListener {

    private MainPresenter presenter;
    private LinearLayoutManager layoutManager;
    private InfoMessage infoMessage;
    public Parcelable listState;
    private ACProgressFlower mLoadingDialog;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private static final String TAG_LOG = "MainActivity";
    private int orientation;

    @BindView(R.id.rvFlicks) RecyclerView flicksRecycleView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new MainPresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        infoMessage = new InfoMessage(this);

        orientation = getResources().getConfiguration().orientation;
        setupRecyclerView(flicksRecycleView);

        swipeRefreshLayout.setOnRefreshListener(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        presenter.loadFlicks();
    }

    public void showLoadingDialog()
    {
        if (mLoadingDialog == null )
        {
            mLoadingDialog = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.RED)
                    .text(getResources().getString(R.string.loading))
                    .fadeColor(Color.DKGRAY).build();
        }

        if (!mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public void hideLoadingDialog()
    {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
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
    public void onRefresh() {
        presenter.loadFlicks();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showFLicks(List<Flick> flickList) {
        FlickAdapter adapter = (FlickAdapter) flicksRecycleView.getAdapter();
        adapter.setFlicks(flickList);
        adapter.notifyDataSetChanged();
        flicksRecycleView.requestFocus();
        flicksRecycleView.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        FlickAdapter adapter = new FlickAdapter(this, this.orientation);
        adapter.setCallback(new FlickAdapter.CallbackListener() {
            @Override
            public void onItemClick(Flick flick) {
                startActivity(FlickActivity.newIntent(MainActivity.this, flick));
            }
        });
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                presenter.loadFlicks();
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    @Override
    public void showMessage(String message) {
        hideLoadingDialog();
        flicksRecycleView.setVisibility(View.INVISIBLE);
        infoMessage.showMessage(message);
    }

    @Override
    public void showProgressIndicator() {
        showLoadingDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
