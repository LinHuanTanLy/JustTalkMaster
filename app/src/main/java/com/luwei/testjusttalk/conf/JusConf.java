package com.luwei.testjusttalk.conf;

/**
 * Created by Administrator on 2016/12/1.
 */
public class JusConf {

    //    justalk appkey
    private static final String key = "a63f044007939b8075234096";
    //    小米推送appid
    private static final String MIPUSH_APPID = "2882303761517416084";
    // 小米推送 appkey
    private static final String MIPUSH_APPKEY = "5711741678084";
    // gcm id
    private static final String GCM_SENDER_ID = "527225413725";

    public static String getKey() {
        return key;
    }

    public static String getMipushAppid() {
        return MIPUSH_APPID;
    }

    public static String getMipushAppkey() {
        return MIPUSH_APPKEY;
    }

    public static String getGcmSenderId() {
        return GCM_SENDER_ID;
    }
}
