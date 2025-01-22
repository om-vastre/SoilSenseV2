package com.example.soilsense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AnalysisActivity extends AppCompatActivity {

    String userNo;
    ImageView ivThumbU, ivThumbD, ivThumbU2, ivThumbD2;
    TextView tvFlowerGender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        userNo = getIntent().getExtras().getString("userNo");

        findViewById(R.id.analysis_backButton).setOnClickListener(v -> AnalysisActivity.super.onBackPressed());

        ivThumbU = findViewById(R.id.ivThumbU);
        ivThumbD = findViewById(R.id.ivThumbD);
        ivThumbU2 = findViewById(R.id.ivThumbU2);
        ivThumbD2 = findViewById(R.id.ivThumbD2);
        tvFlowerGender = findViewById(R.id.tvFlowerGender);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo+"/da");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the values of the dashboard progress bars
                    String decomrate = dataSnapshot.child("decomrate").getValue(String.class);
                    String fpredict = dataSnapshot.child("fpredict").getValue(String.class);
                    String productqut = dataSnapshot.child("productqut").getValue(String.class);


                    AnalysisActivity.this.runOnUiThread(() -> setImg(decomrate, fpredict, productqut));

                } else {
                    Toast.makeText(AnalysisActivity.this, "Dashoard data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(AnalysisActivity.this, "Failed to read dashboard value!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void setImg(String decom, String fpre,String pro){
        if(Objects.equals(decom, "good")){
            ivThumbU2.setImageResource(R.drawable.ic_baseline_thumb_up_filled);
            ivThumbD2.setImageResource(R.drawable.ic_baseline_thumb_down);
        }
        else {
            ivThumbD2.setImageResource(R.drawable.ic_baseline_thumb_down_filled);
            ivThumbU2.setImageResource(R.drawable.ic_baseline_thumb_up);
        }

        if(Objects.equals(pro, "good")){
            ivThumbU.setImageResource(R.drawable.ic_baseline_thumb_up_filled);
            ivThumbD.setImageResource(R.drawable.ic_baseline_thumb_down);
        }
        else {
            ivThumbD.setImageResource(R.drawable.ic_baseline_thumb_down_filled);
            ivThumbU.setImageResource(R.drawable.ic_baseline_thumb_up);
        }

        tvFlowerGender.setText(String.format("%s%s", fpre.substring(0, 1).toUpperCase(), fpre.substring(1)));
    }
}

