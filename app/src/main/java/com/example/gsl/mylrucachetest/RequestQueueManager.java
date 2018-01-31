package com.example.gsl.mylrucachetest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by dell on 2018/1/30.
 */

public class RequestQueueManager {
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(MyApplication.getContext());
    public static void addRequest(Request<?> request,Object object){
        if (object != null){
            request.setTag(object);
        }
        mRequestQueue.add(request);
    }
    public static void cancelAll(Object tag){
        mRequestQueue.cancelAll(tag);
    }
}
