package com.example.djtmedoi.ultil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    String s;
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.getConnectivityStatus(context);
        if(status==1) {
            s = "Wifi Enabled";
        } else if (status==2){
            s = "Mobile data Enabled";
        } else {
            s = "Không có kết nối Internet";
        }
//        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
