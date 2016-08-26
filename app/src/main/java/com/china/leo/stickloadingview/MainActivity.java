package com.china.leo.stickloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.china.leo.stickloadingview.widget.CustomLoadingView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        ((CustomLoadingView)findViewById(R.id.loadingView)).start();
        super.onResume();
    }
}
