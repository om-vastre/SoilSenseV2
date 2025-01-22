package com.example.soilsense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NotiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        findViewById(R.id.noti_backButton).setOnClickListener(v -> NotiActivity.super.onBackPressed());

    }
}


