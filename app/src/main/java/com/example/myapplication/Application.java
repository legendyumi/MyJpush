package com.example.myapplication;

import cn.jpush.android.api.JPushInterface;

/**
 * @author:yumi
 * @description:
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化极光推送
        JPushInterface.setDebugMode(false);
        JPushInterface.init(getApplicationContext());
    }
}
