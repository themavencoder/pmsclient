package com.aloine.genclient;

import android.support.annotation.UiThread;

public interface ProgressCallBack {

    @UiThread
    void showProgressBar();
    @UiThread
    void hideProgressBar();
}
