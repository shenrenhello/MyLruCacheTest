package com.example.gsl.mylrucachetest;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by dell on 2018/1/30.
 */

public class ImageCacheManager {
    private static ImageCacheUtil imageCache = new ImageCacheUtil();
    public static ImageLoader mImageLoader = new ImageLoader(RequestQueueManager.mRequestQueue,imageCache);

    public static void loadImage(String url,ImageLoader.ImageListener imageListener){
        mImageLoader.get(url,imageListener);
    }
    public static void loadImage(String url,ImageLoader.ImageListener imageListener,int maxWith,int maxHeight){
        mImageLoader.get(url,imageListener,maxWith,maxHeight);
    }
}
