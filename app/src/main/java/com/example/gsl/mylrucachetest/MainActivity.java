package com.example.gsl.mylrucachetest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

public class MainActivity extends AppCompatActivity {

    ConnectivityManager connectivityManager;
    Toast toast;
    ImageView imageView;
    String url = "http://img0.bdstatic.com/img/image/shouye/xiaoxiao/%E5%AE%A0%E7%89%A983.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (hasNetWorkConnection()){
            loadImage();
        }else {
            if (toast == null){
                toast = Toast.makeText(MainActivity.this,"请检查网络连接",Toast.LENGTH_SHORT);
            }else {
                //do nothing
            }
            toast.show();
        }
    }

    private boolean hasNetWorkConnection(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable());
    }
    private void loadImage(){
        ImageCacheManager.loadImage(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                //progress.setVisibility(View.GONE);
                if (response.getBitmap() != null){
                    imageView.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


}
