package com.example.soilsense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SensorsActivity extends AppCompatActivity {

    String userNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        userNo = getIntent().getExtras().getString("userNo");

        View.OnClickListener cardClickListener = view -> {
            int id = view.getId();
            switch (id) {
                case R.id.cvETemp:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "etemp").putExtra("userNo", userNo));
                    break;
                case R.id.cvEHum:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "ehumi").putExtra("userNo", userNo));
                    break;
                case R.id.cvSTemp:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "stemp").putExtra("userNo", userNo));
                    break;
                case R.id.cvSHum:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "shumi").putExtra("userNo", userNo));
                    break;
                case R.id.cvEC:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "ec").putExtra("userNo", userNo));
                    break;
                case R.id.cvCN:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "cn").putExtra("userNo", userNo));
                    break;
                case R.id.cvNPK:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "npk").putExtra("userNo", userNo));
                    break;
                case R.id.cvpH:
                    startActivity(new Intent(SensorsActivity.this, DetailsActivity.class).putExtra("sname", "ph").putExtra("userNo", userNo));
                    break;
            }
        };

        findViewById(R.id.cvETemp).setOnClickListener(cardClickListener);
        findViewById(R.id.cvEHum).setOnClickListener(cardClickListener);
        findViewById(R.id.cvSTemp).setOnClickListener(cardClickListener);
        findViewById(R.id.cvSHum).setOnClickListener(cardClickListener);
        findViewById(R.id.cvEC).setOnClickListener(cardClickListener);
        findViewById(R.id.cvCN).setOnClickListener(cardClickListener);
        findViewById(R.id.cvNPK).setOnClickListener(cardClickListener);
        findViewById(R.id.cvpH).setOnClickListener(cardClickListener);

        findViewById(R.id.sensors_backButton).setOnClickListener(v -> SensorsActivity.super.onBackPressed());

//        ETemp.setOnClickListener(cardClickListener);
//        EHum.setOnClickListener(cardClickListener);
//        STemp.setOnClickListener(cardClickListener);
//        SHum.setOnClickListener(cardClickListener);
//        EC.setOnClickListener(cardClickListener);
//        CN.setOnClickListener(cardClickListener);
//        pH.setOnClickListener(cardClickListener);
//        NpK.setOnClickListener(cardClickListener);

    }
}