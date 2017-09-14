package com.example.gkudva.flickviewer.presenter;

/**
 * Created by gkudva on 09/09/17.
 */

public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}
