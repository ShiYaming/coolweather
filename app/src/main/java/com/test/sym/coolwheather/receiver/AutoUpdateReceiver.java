package com.test.sym.coolwheather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.sym.coolwheather.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);

        }
}

