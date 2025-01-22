package com.example.soilsense;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AutoActivity extends AppCompatActivity {

    String userNo, measureFlag;
    private DatabaseReference mDatabaseReference;
    TextView measure, watergiv, tap1, tap2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        userNo = getIntent().getExtras().getString("userNo");
        Bundle args = new Bundle();
        args.putString("userNo", userNo);
        
        measure = findViewById(R.id.tvMeasureWater);
        watergiv = findViewById(R.id.tvWaterGiven);
        tap1 = findViewById(R.id.tvTap1);
        tap2 = findViewById(R.id.tvTap2);

        findViewById(R.id.auto_backButton).setOnClickListener(v -> AutoActivity.super.onBackPressed());

        findViewById(R.id.ivTime).setOnClickListener(v -> {
            TimeBottomSheetFragment timeBottomSheetFragment = new TimeBottomSheetFragment();
            timeBottomSheetFragment.setArguments(args);
            timeBottomSheetFragment.show(getSupportFragmentManager(), timeBottomSheetFragment.getTag());
        });

        findViewById(R.id.ivJar).setOnClickListener(v -> {
            LiterFragmentBottomSheet literBottomSheetFragment = new LiterFragmentBottomSheet();
            literBottomSheetFragment.setArguments(args);
            literBottomSheetFragment.show(getSupportFragmentManager(), literBottomSheetFragment.getTag());
        });

        measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (measureFlag=="0") {
                            FirebaseDatabase.getInstance().getReference(userNo+"/auto/count").setValue("1");
                            measure.setText("Measuring....");
                            measureFlag="1";
                        }else {
                            FirebaseDatabase.getInstance().getReference(userNo+"/auto/count").setValue("0");
                            measure.setText("Measure");
                            measureFlag="0";
                        }
                    }
                });

            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userNo+"/auto/watergiven");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AutoActivity.this.runOnUiThread(() -> watergiv.setText(String.format("%sL water is given to each plant.", dataSnapshot.getValue(String.class))));
                } else {
                    Toast.makeText(AutoActivity.this, "Water given data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(AutoActivity.this, "Failed to read Water given data!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userNo+"/auto/tappos");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AutoActivity.this.runOnUiThread(() -> tap1.setText(dataSnapshot.child("tap1").getValue(String.class)));
                    AutoActivity.this.runOnUiThread(() -> tap2.setText(dataSnapshot.child("tap2").getValue(String.class)));
                } else {
                    Toast.makeText(AutoActivity.this, "Tap data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(AutoActivity.this, "Failed to read tap data!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userNo+"/auto/count");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tem = snapshot.getValue(String.class);
                Log.d("TAG", tem);
                if (Objects.equals(tem, "1")) {
                    AutoActivity.this.runOnUiThread(() ->measure.setText("Measuring...."));
                    measureFlag = "1";
                } else {
                    AutoActivity.this.runOnUiThread(() ->measure.setText("Measure"));
                    measureFlag = "0";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AutoActivity.this, "Failed to read data!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read Measure.", error.toException());
            }
        });

    }
}

