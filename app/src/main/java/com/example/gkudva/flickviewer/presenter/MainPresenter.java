package com.example.gkudva.flickviewer.presenter;

import android.util.Log;

import com.example.gkudva.flickviewer.model.Flick;
import com.example.gkudva.flickviewer.model.FlickService;
import com.example.gkudva.flickviewer.model.Response;
import com.example.gkudva.flickviewer.view.MainMvpView;

import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gkudva on 09/09/17.
 */

public class MainPresenter implements Presenter<MainMvpView> {

    private MainMvpView mainMvpView;
    private Subscription subscription;
    private List<Flick> flickList;
    private FlickService flickService;
    private static final String TAG = "MainPresenter";
    private Response response;
    private static int pageNum = 0;

    @Override
    public void attachView(MainMvpView view) {
        this.mainMvpView = view;
    }

    @Override
    public void detachView() {
        pageNum = 0;
        this.mainMvpView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void loadFlicks()
    {
        pageNum++;
        mainMvpView.showProgressIndicator();
        if (subscription != null) subscription.unsubscribe();

        if (flickService == null)
            flickService = FlickService.Factory.create();

        subscription = flickService.getResponse(Integer.toString(pageNum))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Flicks loaded " + response);
                        if (!flickList.isEmpty()) {
                            mainMvpView.showFLicks(flickList);
                        } else {
                            mainMvpView.showMessage("No Flicks found");
                        }


                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading flicks ", error);
                        if (isHttp404(error)) {
                            mainMvpView.showMessage("HTTP Error");
                        } else {
                            mainMvpView.showMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Response response) {
                        if (flickList == null)
                            MainPresenter.this.flickList = response.getFlicks();
                        else
                            MainPresenter.this.flickList.addAll(response.getFlicks());
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

}