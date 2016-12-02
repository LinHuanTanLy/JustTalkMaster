package com.luwei.testjusttalk.application;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.justalk.cloud.juscall.JusCallConfig;
import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.jusdoodle.DoodleDelegate;
import com.justalk.cloud.juslogin.LoginDelegate;
import com.justalk.cloud.juspush.MiPush;
import com.luwei.testjusttalk.conf.JusConf;

/**
 * Created by Administrator on 2016/12/1.
 */
public class JusApplication extends Application implements LoginDelegate.InitStat {


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > 22 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) return;

        if (LoginDelegate.init(this, JusConf.getKey()) != MTC_INIT_SUCCESS)
            return;
        MtcCallDelegate.init(this);
        JusCallConfig.setBackIntentAction("com.justalk.cloud.sample.call.action.backfromcall");
        MiPush.setCallPushParm();
        DoodleDelegate.init(this);
        String[] imgs = new String[]{"/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_0.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_1.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_2.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_3.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_4.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_5.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_6.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_7.jpg",
                "/sdcard/Android/data/com.justalk.cloud.sample.android.call/files/mtc/bgimage/background_8.jpg"};
        DoodleDelegate.setBackgroundImages(imgs);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LoginDelegate.destroy();
    }
}
