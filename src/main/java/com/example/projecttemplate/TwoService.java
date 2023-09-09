package com.example.projecttemplate;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class TwoService extends Service {
    public TwoService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SmsManager smsManager= SmsManager.getDefault();
        /*
        第一个参数：电话号码
        第二个参数：运营商，传入null就行，系统会自动调用
        第三个参数，短信的内容
        第四个，第五个参数：短信发送状态的广播，这里不用广播，传入null
         */
        Log.d("flag", "onStartCommand: "+intent.getStringExtra("phone")+intent.getStringExtra("sms"));
        smsManager.sendTextMessage(intent.getStringExtra("phone"),null,intent.getStringExtra("sms"),null,null);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}