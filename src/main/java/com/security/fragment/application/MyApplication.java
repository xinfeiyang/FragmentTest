package com.security.fragment.application;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.WindowManager;

import com.security.fragment.receiver.NetworkConnectChangeReceiver;

import cn.sharesdk.framework.ShareSDK;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 在全局的Application中初始化一些东西;
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    //表示网络是否连接
    public boolean isConnected;
    //表示当前网络是否是移动网络
    public boolean isMobile;
    //表示当前网络是否是WiFi
    public boolean isWifi;
    //表示WiFi开关是否打开
    public boolean isEnablaWifi;
    //表示移动网络数据是否打开
    public boolean isEnableMobile;
    private NetworkConnectChangeReceiver receiver;
    private static Context context;
    private static AlertDialog dialog;

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        context=getApplicationContext();
        instance=this;
        registerBroadcastReceiver();
    }

    /**
     * 注册广播;
     */
    private void registerBroadcastReceiver() {
        receiver = new NetworkConnectChangeReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(receiver,filter);
    }

    public static void showWifiSettingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(dialog==null){
            dialog = builder.setTitle("设置")
                    .setMessage("请设置网络")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转到系统的网络设置界面
                            Intent intent = null;
                            // 先判断当前系统版本
                            if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                            } else {
                                intent = new Intent();
                                intent.setClassName("com.android.settings",
                                        Settings.ACTION_WIFI_SETTINGS);
                            }
                            if ((context instanceof Application)) {
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                            }
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null).create();
            //设置为系统的Dialog，这样使用Application的时候不会 报错
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();

    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getInstance(){
        return instance;
    }


    public boolean isConnected() {
        return isWifi||isMobile;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }

    public boolean isWifi() {
        return isWifi;
    }

    public void setWifi(boolean wifi) {
        isWifi = wifi;
    }

    public boolean isEnablaWifi() {
        return isEnablaWifi;
    }

    public void setEnablaWifi(boolean enablaWifi) {
        isEnablaWifi = enablaWifi;
    }

    public boolean isEnableMobile() {
        return isEnableMobile;
    }

    public void setEnableMobile(boolean enableMobile) {
        isEnableMobile = enableMobile;
    }

}
