package com.example.projecttemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

public class ThreeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getAction().equals("com.example.offline")){
            SharedPreferences sp = context.getSharedPreferences( "mydata", Context.MODE_PRIVATE );
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("loginStauts",false);
            editor.apply();
            Toast.makeText(context, "已下线", Toast.LENGTH_SHORT).show();


            ((MainActivity)context).viewPager2.setCurrentItem(0);
        }

    }
}