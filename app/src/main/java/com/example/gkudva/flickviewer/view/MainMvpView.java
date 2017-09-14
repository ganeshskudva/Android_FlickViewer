package com.example.gkudva.flickviewer.view;

import com.example.gkudva.flickviewer.model.Flick;

import java.util.List;

/**
 * Created by gkudva on 09/09/17.
 */

public interface MainMvpView extends MvpView {

    void showFLicks(List<Flick> flickList);

    void showMessage(String message);

    void showProgressIndicator();
}
