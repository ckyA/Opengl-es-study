package com.example.chenkengyu.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService3 extends Service {
    public MyService3() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "MyService3  onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i("MyService", "MyService3 onCreate");
        super.onDestroy();
    }
}
