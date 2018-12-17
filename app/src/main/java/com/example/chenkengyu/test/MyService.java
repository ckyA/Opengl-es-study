package com.example.chenkengyu.test;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "MyService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent nIntent = new Intent(MyService.this, MyService2.class);
                    startService(nIntent);
                }
        }, 60*1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nIntent = new Intent(MyService.this, MyService2.class);
                startService(nIntent);
            }
        }, 2*60*1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nIntent = new Intent(MyService.this, MyService3.class);
                startService(nIntent);
            }
        }, 3*60*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("MyService", "MyService onCreate");
        super.onDestroy();
    }
}
