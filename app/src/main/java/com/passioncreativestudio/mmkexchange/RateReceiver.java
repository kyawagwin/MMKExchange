package com.passioncreativestudio.mmkexchange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RateReceiver extends BroadcastReceiver {
    private static final String TAG = RateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, TAG + " running!", Toast.LENGTH_SHORT).show();
    }
}
