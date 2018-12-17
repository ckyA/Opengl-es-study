package com.example.chenkengyu.test;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService2 extends Service {
    public MyService2() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "MyService2 onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i("MyService", "MyService2 onCreate");
        super.onDestroy();
    }
}
