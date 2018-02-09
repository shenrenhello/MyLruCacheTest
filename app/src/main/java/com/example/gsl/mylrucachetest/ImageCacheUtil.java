package com.example.gsl.mylrucachetest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by dell on 2018/1/25.
 */

public class ImageCacheUtil implements ImageLoader.ImageCache {
    //缓存类
    private static LruCache<String,Bitmap> mLruCache;//内存缓存
    private static DiskLruCache mDiskLruCache;//磁盘缓存

    //磁盘缓存大小
    private static final int DISKMAXSIZE = 10 * 1024 * 1024;

    //

    private String TAG = ImageCacheUtil.class.getSimpleName();

    public ImageCacheUtil(){
        //获取应用可占内存的1/8作为缓存空间代销
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        //实例化LruCache
        mLruCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes()*bitmap.getHeight();//super.sizeOf(key, value);
            }
        };
        //DiskLruCache实例，他的构造方法时私有的，所以我们需要通过它提供的open方法来生成
        try {
            mDiskLruCache = DiskLruCache.open(getDiskCacheDir(MyApplication.getContext(),"bitmap"),getAppVersion(MyApplication.getContext()),1,DISKMAXSIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * volley请求时会先回调getBitmap查看缓存中是否有图片，没有再去请求网络
     * @param url
     * @return
     */
    @Override
    public Bitmap getBitmap(String url) {
        if (mLruCache.get(url) != null){
            Log.d(TAG,"从内存获取");
            return mLruCache.get(url);
        }else {
            String diskKey = hashKeyForDisk(url);
            try {
                if (mDiskLruCache.get(diskKey) != null){//文件中存在
                    Log.d(TAG,"从文件中取");
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(diskKey);
                    Bitmap bitmap = null;
                    if (snapshot != null){
                        bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                        //存入内存
                        mLruCache.put(url,bitmap);
                    }
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * volley下载完图片后会回调putBitmap方法将图片进行缓存
     * @param url
     * @param bitmap
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //存入内存
        Log.d(TAG,"存入内存");
        mLruCache.put(url,bitmap);
        //存入文件
        String diskKey = hashKeyForDisk(url);
        try {
            if (mDiskLruCache.get(diskKey) == null){
                Log.d(TAG,"存入文件");
                DiskLruCache.Editor editor = mDiskLruCache.edit(diskKey);
                if (editor != null){
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)){
                        editor.commit();
                    }else {
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法会判断当前SD卡是否存在，然后选择缓存地址
    public File getDiskCacheDir(Context context,String uniqueName){
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()){
            cachePath = context.getExternalCacheDir().getPath();
        }else {
            cachePath = context.getCacheDir().getPath();
        }
        Log.d(TAG,cachePath+File.separator+uniqueName);
        return new File(cachePath+File.separator+uniqueName);
    }
    //获得应用的version
    public int getAppVersion(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    //MD5加密
    private String hashKeyForDisk(String key){
        String cacheKey;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cacheKey = byteToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key);
            e.printStackTrace();
        }
        return cacheKey;
    }

    private String byteToHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<digest.length;i++){
            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
