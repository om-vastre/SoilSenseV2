package com.example.soilsense;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import android.Manifest;

import androidx.core.app.ActivityCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SecurityService extends Service {

    private static final String CHANNEL_ID = "SOILSENSE_SECURITY";
    private static final Integer NOTIFICATION_ID = 44;
    String userName, userNo;
    MediaPlayer mpAlert;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userName = intent.getStringExtra("userName");
        mpAlert = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
        mpAlert.setLooping(true);
        mpAlert.setVolume(1.0f, 1.0f);

        FirebaseDatabase.getInstance().getReference("/users/" + userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNo = snapshot.getValue(String.class);
                Log.d("userNo", "User : " + userNo);
                setAlert();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to find user in Users DB!.", Toast.LENGTH_LONG).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        return START_STICKY;
    }

    private void setAlert() {
        FirebaseDatabase.getInstance().getReference(userNo + "/security").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Security Val", "SecVal : " + snapshot.getValue(String.class));

                if (Objects.equals(snapshot.getValue(String.class), "1")) {
                    mpAlert = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
                    mpAlert.setLooping(true);
                    mpAlert.start();
                    mpAlert.setVolume(1.0f, 1.0f);
                    mpAlert.start();
                    showNotification();
                } else if (Objects.equals(snapshot.getValue(String.class), "0")) {
                    mpAlert.pause();
                    mpAlert.stop();
                    mpAlert.release();
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(44);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showNotification() {


        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.icon2_512, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();


//        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setLargeIcon(largeIcon)
//                .setSmallIcon(R.drawable.icon2_512)
//                .setContentTitle("SoilSense Device At Risk!!!")
//                .setContentText("Detected unusual movement of device")
//                .setSubText("Security Alert")
//                .addAction(R.drawable.button_noti, "Stop Alarm", stopButton())
//                .setPriority(NotificationCompat.PRIORITY_HIGH);


        Log.d("User No", "SecService: "+userNo);

        Intent stopIntent = new Intent(this, MyReceiver.class);
        stopIntent.setAction("STOP_ALARM");
        stopIntent.setData(Uri.parse("userNo:" + userNo));

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, FLAG_IMMUTABLE);

        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(R.drawable.button_noti, "Stop Alert", stopPendingIntent).build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Security Channel", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.icon2_512)
                .setContentTitle("Device At Risk!!!")
                .setContentText("Security Alert")
                .setSubText("Detected unusual movement of device")
                .addAction(stopAction)
                .setAutoCancel(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Security Service", "Destroyed Security Service");
    }

}