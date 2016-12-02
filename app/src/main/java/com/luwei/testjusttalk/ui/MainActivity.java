package com.luwei.testjusttalk.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.justalk.cloud.juscall.JusCallConfig;
import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcCallDb;
import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcMediaConstants;
import com.luwei.testjusttalk.R;
import com.luwei.testjusttalk.base.baseactivity;
import com.luwei.testjusttalk.utils.Helper;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MainActivity extends baseactivity {

    private EditText mEtTalkNumber;
    private Button mBtTalkAudio;
    private Button mBtTalkVideo;
    private RadioGroup mRgTalkBity;
    private RadioButton mRbTalkBityNano;
    private RadioButton mRbTalkBityMin;
    private RadioButton mRbTalkBityLow;
    private RadioButton mRbTalkBityMid;
    private RadioButton mRbTalkBity720p;
    private RadioGroup mRgTalkMack;
    private RadioButton mRbTalkMackLow;
    private RadioButton mRbTalkMackMid;
    private RadioButton mRbTalkMackHigh;
    private RadioGroup mRgTalkAuto;
    private RadioButton mRbTalkAutoOff;
    private RadioButton mRbTalkAutoAudio;
    private RadioButton mRbTalkAutoVideo;
    private Button mBtTalkDoodle;
    private Button mBtTalkRecord;

    //    通话媒体已接通事件
    private static BroadcastReceiver sMtcCallTalkingReceiver;
    //    通话结束事件，对方挂断
    private static BroadcastReceiver sMtcCallTermedReceiver;
    //    通话结束事件，主动挂断
    private static BroadcastReceiver sMtcCallDidTermReceiver;
    //    通话呼入事件
    private static BroadcastReceiver sMtcInComeReceiver;
    //    通话呼出事件
    private static BroadcastReceiver sMtcOutGoingReceiver;
    //    被叫振铃事件
    private static BroadcastReceiver sMtcAlertReceiver;
    //    通话已建立事件
    private static BroadcastReceiver sMtcConnectingReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        if (MtcCli.Mtc_CliGetState() != MtcCliConstants.EN_MTC_CLI_STATE_LOGINED) {
            finish();
            return;
        }
        checkPermission();
        initBroadCastReceiver();
    }

    private void initBroadCastReceiver() {

//        通话媒体已接通事件
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        if (sMtcCallTalkingReceiver == null) {
            sMtcCallTalkingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showLogAndTs(" 通话媒体已接通事件");
                    int dwCallId = MtcConstants.INVALIDID;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    showLogAndTs(Helper.getSendDir(context) + System.currentTimeMillis() + ".avi");
                    //调用开始录制接口Mtc_CallRecRecvVideoStart
                    int result_code = MtcCall.Mtc_CallRecRecvVideoStart(dwCallId, Helper.getSendDir(context) + System.currentTimeMillis() + ".avi", MtcMediaConstants.EN_MTC_MFILE_AVI_H264,
                            300, "xxxx");
                    Log.e(TAG, "onReceive for start: " + result_code);
                    if (result_code == MtcConstants.ZOK) {
                        showLogAndTs("ok");
                    } else if (result_code == MtcConstants.ZFAILED) {
                        showLogAndTs("ZFAILED");
                    } else {
                        showLogAndTs("INVALIDID");
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallTalkingReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallTalkingNotification));
        }


//            通话结束事件，对方挂断
        if (sMtcCallTermedReceiver == null) {
            sMtcCallTermedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    showLogAndTs("通话结束事件，对方挂断");
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    //这里要先判断下 callId 是否和通话开始的那个callId 一致，如果一致才调用停止录制接口
                    int result_code = MtcCall.Mtc_CallRecRecvVideoStop(dwCallId);
                    Log.e(TAG, "onReceive for Mtc_CallRecRecvVideoStop: " + result_code);
                }
            };
            broadcastManager.registerReceiver(sMtcCallTermedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallTermedNotification));
        }


//             通话结束事件，主动挂断
        if (sMtcCallDidTermReceiver == null) {
            sMtcCallDidTermReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    showLogAndTs("通话结束事件，主动挂断");
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    //这里要先判断下 callId 是否和通话开始的那个callId 一致，如果一致才调用停止录制接口
                    int result_code = MtcCall.Mtc_CallRecRecvVideoStop(dwCallId);
                    Log.e(TAG, "onReceive for Mtc_CallRecRecvVideoStop: " + result_code);
                }
            };
            broadcastManager.registerReceiver(sMtcCallDidTermReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallDidTermNotification));
        }
//        通话呼入事件
        if (sMtcInComeReceiver == null) {
            sMtcInComeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showLogAndTs("有通话呼入了");
                }
            };
            broadcastManager.registerReceiver(sMtcInComeReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallIncomingNotification));
        }
//        通话呼出
        if (sMtcOutGoingReceiver == null) {
            sMtcOutGoingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showLogAndTs("有通话呼出了");
                }
            };
            broadcastManager.registerReceiver(sMtcOutGoingReceiver, new IntentFilter(MtcCallConstants.MtcCallOutgoingNotification));
        }

//        被叫振铃
        if (sMtcAlertReceiver == null) {
            sMtcAlertReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showLogAndTs("被叫振铃");
                }
            };
            broadcastManager.registerReceiver(sMtcAlertReceiver, new IntentFilter(MtcCallConstants.MtcCallAlertedNotification));
        }
        //    通话已建立事件
        if (sMtcConnectingReceiver == null) {
            sMtcConnectingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showLogAndTs("通话已建立事件");
                }
            };
            broadcastManager.registerReceiver(sMtcConnectingReceiver, new IntentFilter(MtcCallConstants.MtcCallConnectingNotification));
        }
    }


    public static void toMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void assignViews() {
        mEtTalkNumber = (EditText) findViewById(R.id.et_talk_number);
        mBtTalkAudio = (Button) findViewById(R.id.bt_talk_audio);
        mBtTalkAudio.setOnClickListener(this);
        mBtTalkVideo = (Button) findViewById(R.id.bt_talk_video);
        mBtTalkVideo.setOnClickListener(this);
        mRgTalkBity = (RadioGroup) findViewById(R.id.rg_talk_bity);
        mRbTalkBityNano = (RadioButton) findViewById(R.id.rb_talk_bity_nano);
        mRbTalkBityMin = (RadioButton) findViewById(R.id.rb_talk_bity_min);
        mRbTalkBityLow = (RadioButton) findViewById(R.id.rb_talk_bity_low);
        mRbTalkBityMid = (RadioButton) findViewById(R.id.rb_talk_bity_mid);
        mRbTalkBity720p = (RadioButton) findViewById(R.id.rb_talk_bity_720p);
        mRgTalkMack = (RadioGroup) findViewById(R.id.rg_talk_mack);
        mRbTalkMackLow = (RadioButton) findViewById(R.id.rb_talk_mack_low);
        mRbTalkMackMid = (RadioButton) findViewById(R.id.rb_talk_mack_mid);
        mRbTalkMackHigh = (RadioButton) findViewById(R.id.rb_talk_mack_high);
        mRgTalkAuto = (RadioGroup) findViewById(R.id.rg_talk_auto);
        mRbTalkAutoOff = (RadioButton) findViewById(R.id.rb_talk_auto_off);
        mRbTalkAutoAudio = (RadioButton) findViewById(R.id.rb_talk_auto_audio);
        mRbTalkAutoVideo = (RadioButton) findViewById(R.id.rb_talk_auto_video);
        mBtTalkDoodle = (Button) findViewById(R.id.bt_talk_doodle);
        mBtTalkDoodle.setOnClickListener(this);
        mBtTalkRecord = (Button) findViewById(R.id.bt_talk_record);
        mBtTalkRecord.setOnClickListener(this);


        int mode = JusCallConfig.getBitrateMode();
        switch (mode) {
            case JusCallConfig.BITRATE_MODE_NANO:
                mRgTalkBity.check(R.id.rb_talk_bity_nano);
                break;
            case JusCallConfig.BITRATE_MODE_MIN:
                mRgTalkBity.check(R.id.rb_talk_bity_min);
                break;
            case JusCallConfig.BITRATE_MODE_LOW:
                mRgTalkBity.check(R.id.rb_talk_bity_low);
                break;
            case JusCallConfig.BITRATE_MODE_NORMAL:
                mRgTalkBity.check(R.id.rb_talk_bity_mid);
                break;
            case JusCallConfig.BITRATE_MODE_720P:
                mRgTalkBity.check(R.id.rb_talk_bity_720p);
                break;
            default:
                break;
        }
        // 比特率
        mRgTalkBity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int value = 0;
                switch (checkedId) {
                    case R.id.rb_talk_bity_nano:
                        value = JusCallConfig.BITRATE_MODE_NANO;
                        break;
                    case R.id.rb_talk_bity_min:
                        value = JusCallConfig.BITRATE_MODE_MIN;
                        break;
                    case R.id.rb_talk_bity_low:
                        value = JusCallConfig.BITRATE_MODE_LOW;
                        break;
                    case R.id.rb_talk_bity_mid:
                        value = JusCallConfig.BITRATE_MODE_NORMAL;
                        break;
                    case R.id.rb_talk_bity_720p:
                        value = JusCallConfig.BITRATE_MODE_720P;
                        break;
                    default:
                        break;
                }
                JusCallConfig.setBitrateMode(value);
            }
        });
        // 比特率
        mRgTalkMack.check(R.id.rb_talk_mack_low);
        mRgTalkMack.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int value = 300;
                switch (checkedId) {
                    case R.id.rb_talk_mack_low:
                        value = 300;
                        break;
                    case R.id.rb_talk_mack_mid:
                        value = 500;
                        break;
                    case R.id.rb_talk_mack_high:
                        value = 800;
                        break;
                }
                MtcCallDb.Mtc_CallDbSetVideoNackRttRange(100, value);
            }
        });
        // mack

        boolean autoAnswer = JusCallConfig.getIsAutoAnswerEnable();
        boolean autoAnswerWithVideo = JusCallConfig.getIsAutoAnswerWithVideo();
        if (autoAnswer) {
            if (autoAnswerWithVideo) {
                mRgTalkAuto.check(R.id.rb_talk_auto_video);
            } else {
                mRgTalkAuto.check(R.id.rb_talk_auto_audio);
            }
        } else {
            mRgTalkAuto.check(R.id.rb_talk_auto_off);
        }
        mRgTalkAuto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                // TODO Auto-generated method stub
                boolean enable = false;
                boolean video = false;
                switch (id) {
                    case R.id.rb_talk_auto_off:
                        enable = false;
                        video = false;
                        break;
                    case R.id.rb_talk_auto_audio:
                        enable = true;
                        video = false;
                        break;
                    case R.id.rb_talk_auto_video:
                        enable = true;
                        video = true;
                        break;
                }
                JusCallConfig.setAutoAnswer(enable, video);
            }
        });
        // auto


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_talk_audio:
                audioTalk();
                break;
            case R.id.bt_talk_video:
                videoTalk();
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    private void audioTalk() {
        String number = mEtTalkNumber.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please enter number", Toast.LENGTH_SHORT).show();
            return;
        }
        // 是否需要旋转功能
        JusCallConfig.setCapacityEnabled(JusCallConfig.MAGNIFIER_ENABLED_KEY, false);
        MtcCallDelegate.call(number, null, null, false, null);
    }

    /**
     *
     */
    private void videoTalk() {
        String number = mEtTalkNumber.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please enter number", Toast.LENGTH_SHORT).show();
            return;
        }
        // 是否需要旋转功能
        JusCallConfig.setCapacityEnabled(JusCallConfig.MAGNIFIER_ENABLED_KEY, false);
        MtcCallDelegate.call(number, null, null, true, null);
    }


    /**
     * 权限确认
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return;
        boolean noRecord = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        boolean noCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
        if (noRecord && noCamera) {
            String[] permissions = new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else if (noRecord) {
            String[] permissions = new String[]{android.Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else if (noCamera) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }


    private void showLogAndTs(String... msg) {
        for (String str : msg) {
            Log.e(TAG, "showLogAndTs: " + str);
            showTs(str.toString());
        }

    }
}
