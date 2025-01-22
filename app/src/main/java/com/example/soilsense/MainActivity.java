package com.example.soilsense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final Integer PERMISSION_NOTIFICATION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        replaceFragment(new HomeFragment());

        Log.d("Email", "onCreate: "+getIntent().getExtras().getString("email"));


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_NOTIFICATION_CODE);
            return;
        } else {
            // Permission is granted
        Intent intent = new Intent(MainActivity.this, SecurityService.class);
        intent.putExtra("userName", getIntent().getExtras().getString("email"));
        MainActivity.this.startService(intent);
        }


        ImageView BtnNotification = findViewById(R.id.notification);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        BtnNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotiActivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.menu_crop:
                    replaceFragment(new CropFragment());
                    break;
                case R.id.menu_user:
                    replaceFragment(new UserFragment());
                    break;
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        Bundle args = new Bundle();
        args.putString("userName", getIntent().getExtras().getString("email"));
        fragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_frame_layout, fragment);
        fragmentTransaction.commit();
    }

}

