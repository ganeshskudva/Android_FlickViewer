package com.example.gkudva.flickviewer.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by gkudva on 14/09/17.
 */

public class InfoMessage {
    private Context context;

    public InfoMessage(Context context) {
        this.context = context;
    }

    public void showMessage(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
