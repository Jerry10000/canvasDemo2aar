package com.hanvon.canvasdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

/**
 * Created by pc on 2017/11/3.
 */

public abstract class BaseActivity extends Activity {
    private int sss = 0;
    public  int ddd = 1;
    public void add(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initViews(savedInstanceState);
        loadData();


    }

    protected abstract void initVariables();
    protected abstract void initViews(Bundle savedInstanceState);
    protected abstract void loadData();


}
