package com.example.gsl.mylrucachetest;

import android.app.Application;
import android.content.Context;

/**
 * Created by dell on 2018/1/30.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return MyApplication.context;
    }
}
