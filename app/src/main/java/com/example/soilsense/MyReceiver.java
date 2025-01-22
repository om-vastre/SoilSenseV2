package com.example.soilsense;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyReceiver extends BroadcastReceiver {

    String userNo;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("STOP_ALARM")) {

            Uri dataUri = intent.getData();
            if (dataUri != null && dataUri.getScheme().equals("userNo")){
                userNo = dataUri.getSchemeSpecificPart();

                DatabaseReference securityRef = FirebaseDatabase.getInstance().getReference(userNo+"/security");
                securityRef.setValue("0").addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Alert stopped", Toast.LENGTH_SHORT).show();
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(44);
                }).addOnFailureListener(e -> Toast.makeText(context, "Failed to stop alert!", Toast.LENGTH_SHORT).show());
            }

        }
    }
}

