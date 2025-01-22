package com.example.soilsense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class DetailsActivity extends AppCompatActivity {

    GraphView graphView;
    String sname, sval, userNo;
    CircularProgressIndicator pb;
    TextView tvPBVal;
    SeekBar Sbar;
    Integer arrHistory[] = {0, 0, 0, 0, 0, 0, 0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        sname = getIntent().getExtras().getString("sname");
        userNo = getIntent().getExtras().getString("userNo");

        findViewById(R.id.sensors_backButton).setOnClickListener(v -> DetailsActivity.super.onBackPressed());

        changeTitle_Sub();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo+"/sensors/"+sname);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the values of the dashboard progress bars
                    sval = dataSnapshot.getValue(String.class);

                    Log.d("Firebase", "Sensor Value: " + sval);

                    if(Objects.equals(sval, "nan")){
                        sval = "0000";
                    }
                    DetailsActivity.this.runOnUiThread(() ->setPB(sval));
                    DetailsActivity.this.runOnUiThread(() ->setDetails());
                    DetailsActivity.this.runOnUiThread(() ->getGraph());

                } else {
                    Toast.makeText(DetailsActivity.this, "Details data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(DetailsActivity.this, "Failed to read details!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }

    private void changeTitle_Sub(){
        TextView title = findViewById(R.id.sensors_appbar_title);
        TextView subtitle = findViewById(R.id.sensors_appbar_sybtitle);
        TextView Mtitle = findViewById(R.id.tvMtitle);
        TextView tvMsubtitle = findViewById(R.id.tvMsubtitle);

        switch (sname) {
            case "ec":
                title.setText("EC");
                Mtitle.setText("Electrical Conductivity Status");
                break;
            case "cn":
                title.setText("CN");
                Mtitle.setText("Carbon-to-Nitrogen Ratio");
                break;
            case "etemp":
                title.setText("Temerature");
                Mtitle.setText("Air Temperature Status");
                break;
            case "ehumi":
                title.setText("Humidity");
                Mtitle.setText("Environmental Humidity Status");
                break;
            case "pwet":
                title.setText("Plant Wetness");
                Mtitle.setText("Plant Wetness Status");
                break;
            case "stemp":
                title.setText("Soil Temperature");
                Mtitle.setText("Soil Temperature Status");
                break;
            case "shumi":
                title.setText("Soil Moisture");
                Mtitle.setText("Soil Moisture Status");
                break;
            case "ph":
                title.setText("Water pH");
                Mtitle.setText("Water pH level");
                break;
            case "npk":
                title.setText("N : P : K");
                Mtitle.setText("N : P : K Ratio");
                break;
        }

        subtitle.setText(String.format("Today %s", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime())));
        tvMsubtitle.setText(String.format("Updated Today at %s", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime())));
    }

    private void setPB(String pbVal){
        pb = findViewById(R.id.details_CProgressBar);
        tvPBVal = findViewById(R.id.tvCPBarVal);
        Sbar = findViewById(R.id.detailsSeekBar);

        switch (sname) {

            case "cn":
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if(pbVal=="better") {
                    pb.setProgress(95);
                    Sbar.setProgress(95);
                    tvPBVal.setText("95");
                } else if (pbVal=="good") {
                    pb.setProgress(75);
                    Sbar.setProgress(75);
                    tvPBVal.setText("75");
                } else if (pbVal=="moderate") {
                    pb.setProgress(50);
                    Sbar.setProgress(50);
                    tvPBVal.setText("50");
                }else {
                    pb.setProgress(30);
                    Sbar.setProgress(30);
                    tvPBVal.setText("30");
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "ec":
                pb.setProgress( (int) Double.parseDouble(pbVal)*10 );
                Sbar.setProgress( (int) Double.parseDouble(pbVal)*10 );
                tvPBVal.setText(pbVal);
                if ( Double.parseDouble(pbVal)> 2.5){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                } else{
                    pb.setIndicatorColor(Color.parseColor("#26D192"));
                }
                break;

            case "etemp":
                pb.setProgress((int) Double.parseDouble(pbVal));
                Sbar.setProgress((int) Double.parseDouble(pbVal));
                tvPBVal.setText(String.format("%s°C",pbVal));
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if(Double.parseDouble(pbVal)>40.00){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "ehumi":
                pb.setProgress((int) Double.parseDouble(pbVal));
                Sbar.setProgress((int) Double.parseDouble(pbVal));
                tvPBVal.setText(String.format("%s%%",pbVal));
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if( Double.parseDouble(pbVal)>70.0 ){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "pwet":
                pb.setProgress((int) Double.parseDouble(pbVal));
                Sbar.setProgress((int) Double.parseDouble(pbVal));
                tvPBVal.setText(String.format("%s%%",pbVal));
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if( Double.parseDouble(pbVal)<35.0 ){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "stemp":
                pb.setProgress((int) Double.parseDouble(pbVal));
                Sbar.setProgress((int) Double.parseDouble(pbVal));
                tvPBVal.setText(String.format("%s°C",pbVal));
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if(Double.parseDouble(pbVal)>42.00){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "shumi":
                pb.setProgress( (int) Double.parseDouble(pbVal));
                Sbar.setProgress( (int) Double.parseDouble(pbVal));
                tvPBVal.setText(String.format("%s%%",pbVal));
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if( Double.parseDouble(pbVal)<30){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "ph":
                pb.setProgress((int) (Double.parseDouble(pbVal)*100.0/14));
                Sbar.setProgress((int) (Double.parseDouble(pbVal)*100.0/14));
                tvPBVal.setText(pbVal);
                pb.setIndicatorColor(Color.parseColor("#26D192"));
                if(Double.parseDouble(pbVal)< 6.5 || Double.parseDouble(pbVal)>7.5){
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;

            case "npk":
                if(Objects.equals(pbVal, "nan")){
                    pbVal="0-0-0";
                }
                double temp = Arrays.stream(pbVal.split("-"))
                        .mapToInt(Integer::parseInt)
                        .sum();
                Log.d("temp", "Temp = "+temp);
                pb.setProgress((int) temp/30);
                Sbar.setProgress((int) temp/30);
                tvPBVal.setText(Arrays.stream(pbVal.split("-")).map(s -> String.valueOf( Integer.parseInt(s)/100)).collect(Collectors.joining(":")));
                if ( temp > 1750.00 && temp < 2050.00){
                    pb.setIndicatorColor(Color.parseColor("#26D192"));
                } else{
                    pb.setIndicatorColor(Color.parseColor("#FF0000"));
                }
                break;
        }
    }

    private void setDetails(){
        TextView detai = findViewById(R.id.tvDetails);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo+"/sensordetail/"+sname);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the values of the dashboard progress bars
                    detai.setText(dataSnapshot.getValue(String.class));
                } else {
                    Toast.makeText(DetailsActivity.this, "InDetails data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(DetailsActivity.this, "Failed to read indetails!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }

    private void getGraph() {
        graphView = findViewById(R.id.gvHistory);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userNo + "/history/" + sname);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int[] intArray = new int[(int) dataSnapshot.getChildrenCount()];
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    intArray[i++] = Integer.parseInt(snapshot.getValue(String.class));
                }

                if (arrHistory == null) {
                    arrHistory = new Integer[7];
                    Arrays.fill(arrHistory, 0);
                }

                System.out.println("1 : : : : "+Arrays.toString(intArray));
                DetailsActivity.this.runOnUiThread(() ->setGraph(intArray));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // errors
                Toast.makeText(DetailsActivity.this, "Failed to read History!", Toast.LENGTH_LONG).show();
                Log.w("Firebase", "Failed to read History!.", databaseError.toException());
            }
        });
    }

    private void setGraph(int[] valGraph){
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, valGraph[0]),
                new DataPoint(2, valGraph[1]),
                new DataPoint(3, valGraph[2]),
                new DataPoint(4, valGraph[3]),
                new DataPoint(5, valGraph[4]),
                new DataPoint(6, valGraph[5]),
                new DataPoint(7, valGraph[6])
        });
        series.setThickness(1);
        series.setDataPointsRadius(8);
        series.setDrawDataPoints(true);
        series.setDrawBackground(true);
        series.setBackgroundColor(R.color.purple_200);

        graphView.setTitle("Graph of last 7 days");
        graphView.setTitleColor(R.color.black);
        graphView.setTitleTextSize(24);
        graphView.addSeries(series);
        graphView.getViewport().setMaxXAxisSize(100);
        graphView.getViewport().setMaxYAxisSize(100);
        graphView.getViewport().setScalable(true);

//        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
//        gridLabelRenderer.setHumanRounding(false);
//        gridLabelRenderer.setVerticalAxisTitle("Temperature (°C)");
//        gridLabelRenderer.setVerticalLabelsAlign(Paint.Align.RIGHT);
//        gridLabelRenderer.setHorizontalAxisTitle("Dates");
//        gridLabelRenderer.setVerticalLabelsAlign(Paint.Align.LEFT);

        series.setOnDataPointTapListener((series1, dataPoint) -> Toast.makeText(DetailsActivity.this, "X :"+ dataPoint.getX() + "\nY: "+ dataPoint.getY(), Toast.LENGTH_SHORT).show());

    }
}
