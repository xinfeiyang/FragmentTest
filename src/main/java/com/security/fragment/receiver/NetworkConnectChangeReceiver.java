package com.security.fragment.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import com.security.fragment.application.MyApplication;

/**
 * 网络改变监控广播
 * 监听网络的改变状态,只有在用户操作网络连接开关(wifi,mobile)的时候接受广播,
 * 然后对相应的界面进行相应的操作，并将 状态 保存在我们的MyApplication里面
 */
public class NetworkConnectChangeReceiver extends BroadcastReceiver {

    public static final String TAG="Fragment";

    @Override
    public void onReceive(Context context,Intent intent) {
        //android.net.wifi.WIFI_STATE_CHANGED:监听wifi的连接状态即是否连上了一个有效无线路由
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG,"WIFIState:"+wifiState);
            switch (wifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    MyApplication.getInstance().setEnablaWifi(false);
                    break;

                case WifiManager.WIFI_STATE_DISABLING:
                    break;

                case WifiManager.WIFI_STATE_ENABLING:
                    break;

                case WifiManager.WIFI_STATE_ENABLED:
                    MyApplication.getInstance().setEnablaWifi(true);
                    break;

                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
                default:
                    break;
            }
        }

        //android.net.wifi.STATE_CHANGE:监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;//当然，这边可以更精确的确定状态
                Log.i(TAG, "isConnected:" + isConnected);
                if (isConnected) {
                    MyApplication.getInstance().setWifi(true);
                } else {
                    MyApplication.getInstance().setWifi(false);
                }
            }
        }

        //android.net.conn.CONNECTIVITY_CHANGE:监听网络连接的设置，包括wifi和移动数据的打开和关闭。
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.i(TAG, "CONNECTIVITY_ACTION");
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if(activeNetwork!=null){
                if(activeNetwork.isConnected()){
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        //connected to wifi
                        MyApplication.getInstance().setMobile(false);
                        MyApplication.getInstance().setWifi(true);
                        Log.i(TAG, "当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        //connected to the mobile provider's data plan
                        MyApplication.getInstance().setMobile(true);
                        MyApplication.getInstance().setWifi(false);
                        Log.i(TAG,"当前移动网络连接可用");
                    }
                }else{
                    Log.i(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                    MyApplication.getInstance().setWifi(false);
                    MyApplication.getInstance().setMobile(false);
                }
            }else{
                Log.i(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                MyApplication.getInstance().setWifi(false);
                MyApplication.getInstance().setMobile(false);
            }
        }
    }
}
