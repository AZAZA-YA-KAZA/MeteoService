package com.example.meteoservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeteoService extends Service {
    Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String response = (String) msg.obj;

                Intent intent = new Intent("MeteoService");
                intent.putExtra("INFO", response);
                sendBroadcast(intent);
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread weather_thread = new Thread(new HttpsRequest(handler));
        weather_thread.start();

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}