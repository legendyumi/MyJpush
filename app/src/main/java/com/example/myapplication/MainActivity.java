package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.jpush.android.api.JPushInterface;

/**
 * @author:yumi
 * @description: 首页:测试极光推送
 */
public class MainActivity extends Activity {
    private TextView test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.tv_test);
        test.setText("极光id:" + JPushInterface.getRegistrationID(this));
    }
}
