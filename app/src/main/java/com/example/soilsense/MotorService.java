package com.example.soilsense;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class MotorService extends Service {

    String userNo;
    private Handler mHandler;
    private DatabaseReference mDatabaseReference;
    private boolean mIsRunning;
    long TIME_1, TIME_2;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TIME_1 = intent.getLongExtra("start_time", 0);
        TIME_2 = intent.getLongExtra("stop_time", 0);
        userNo = intent.getStringExtra("userNo");

        mHandler = new Handler();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userNo+"/auto/motor/STATUS");

        if (!mIsRunning) {
            mIsRunning = true;

            Log.d("Motor Service", "TIME_1 : "+TIME_1);
            Log.d("Motor Service", "TIME_2 : "+TIME_2);
            Log.d("Motor Service", "System : "+System.currentTimeMillis());

            mHandler.postDelayed(mRunnable1, TIME_1 - System.currentTimeMillis());
            mHandler.postDelayed(mRunnable2, TIME_2 - System.currentTimeMillis());
        }
        return START_STICKY;
    }

    private Runnable mRunnable1 = new Runnable() {
        @Override
        public void run() {
            Log.d("Motor Service", "in mRunnable1...........");
            mDatabaseReference.setValue("ON");
            Toast.makeText(getApplicationContext(), "Motor Started", Toast.LENGTH_LONG).show();
            mHandler.postDelayed(mRunnable2, TIME_2 - TIME_1);
            Log.d("Motor Service", "in mRunnable1...........");
        }
    };

    private Runnable mRunnable2 = new Runnable() {
        @Override
        public void run() {
            Log.d("Motor Service", "in mRunnable2...........");
            mDatabaseReference.setValue("OFF");
            Toast.makeText(getApplicationContext(), "Motor Stopped", Toast.LENGTH_LONG).show();
            stopSelf();
            Log.d("Motor Service", "in mRunnable2...........");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable1);
        mHandler.removeCallbacks(mRunnable2);
        mIsRunning = false;
        mDatabaseReference.setValue("OFF");
        Log.d("Motor Service", "Destroyed service....");
    }

}

