package com.luwei.testjusttalk.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/1.
 */
public abstract class baseactivity extends AppCompatActivity implements View.OnClickListener {
    protected Context context;
    protected static final String TAG = "LHT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    protected abstract void assignViews();

    protected void showTs(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
