package com.example.soilsense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.Manifest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SprayActivity extends AppCompatActivity {


    TextView tvToday, tvWeek, tvpwet, tvSpray, tvMeasure, tvSprayTime;
    SeekBar seekBarDay, seekBarWeek;
    String userNo, url;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spray);

        userNo = getIntent().getExtras().getString("userNo");

        findViewById(R.id.spray_backButton).setOnClickListener(v -> SprayActivity.super.onBackPressed());

        tvToday = findViewById(R.id.textView22);
        tvWeek = findViewById(R.id.textView23);
        tvpwet = findViewById(R.id.textView27);
        tvSpray = findViewById(R.id.tvSpray);
        tvSprayTime = findViewById(R.id.tvSprayTime);
        tvMeasure = findViewById(R.id.tvMeasure);
        seekBarDay = findViewById(R.id.seekBarDay);
        seekBarWeek = findViewById(R.id.seekBarWeek);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo + "/waterman");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String sprayman = dataSnapshot.child("sprayman").getValue(String.class);
                    String watergiven = dataSnapshot.child("watergiven").getValue(String.class);
                    String week = dataSnapshot.child("week").getValue(String.class);


                    SprayActivity.this.runOnUiThread(() -> setSpray(sprayman, watergiven, week));

                } else {
                    Toast.makeText(SprayActivity.this, "Dashoard data not found!", Toast.LENGTH_LONG).show();
                    Log.d("Firebase", "No data available");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(SprayActivity.this, "Failed to read dashboard value!", Toast.LENGTH_LONG).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        checkPermi();

    }


    private void setSpray(String spray, String Watday, String WatWeek) {

        seekBarDay.setProgress((100 - 0) * ((int) Double.parseDouble(Watday) - 12) / (20 - 12), true);
        seekBarWeek.setProgress((100 - 0) * ((int) Double.parseDouble(WatWeek) - 90) / (140 - 90), true);

        tvSpray.setText(String.format("%sx Concentration is needed to increase for spray during rainy season.", spray));
        tvToday.setText(String.format("Today\n(%sL)", Watday));
        tvWeek.setText(String.format("Week\n(%sL)", WatWeek));


        FirebaseDatabase.getInstance().getReference(userNo + "/sensors/pwet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvpwet.setText(String.format("%s%% Wet", dataSnapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(SprayActivity.this, "Failed to read wetness value!", Toast.LENGTH_LONG).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

    }


    private class WeatherApiTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String jsonString = stringBuilder.toString();
                return new JSONObject(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONArray forecastdayArray = jsonObject.getJSONObject("forecast").getJSONArray("forecastday");
                    for (int i = 1; i < forecastdayArray.length(); i++) {
                        JSONObject forecastdayObject = forecastdayArray.getJSONObject(i);
                        if (forecastdayObject.has("hour")) {
                            JSONArray hourArray = forecastdayObject.getJSONArray("hour");
                            for (int j = 9; j < hourArray.length(); j++) {
                                JSONObject hourObject = hourArray.getJSONObject(j);
                                String time = hourObject.getString("time");
                                String condition = hourObject.getJSONObject("condition").getString("text");
                                Log.d("Weather Data", "Time : " + time + ", Condition : " + condition);

                                if (condition.equals("Moderate rain at times") || condition.equals("Patchy rain possible") || condition.equals("Patchy light drizzle")) {

                                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);

                                    Calendar calendar = Calendar.getInstance();
                                    assert date != null;
                                    calendar.setTime(date);

                                    String rainTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());

                                    calendar.add(Calendar.HOUR_OF_DAY, -5);
                                    String sprayTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());

                                    Log.d("Rainy Weather Data", "Rain Time : " + time + ", Condition : " + condition + ", Spray Time : " + sprayTime);

                                    tvSprayTime.setText(sprayTime.concat(" is good time to spray tomorrow.\nRainfall is predicted tomorrow at " + rainTime));
                                    break;
                                } else {
                                    tvSprayTime.setText("The entire day is a good to spray tomorrow.\nThere is no rainfall predicted for tomorrow.");
                                }

                            }
                        }
                    }
                } catch (JSONException | ParseException e) {
                    Log.d("Weather Error", e.getMessage());
                }
            }
        }
    }


    private void checkPermi() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            runAPITask();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runAPITask();

            } else {
                tvSprayTime.setText("Please grant location permission!");
            }
        }
    }

    private void runAPITask() {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermi();
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                    url = "http://api.weatherapi.com/v1/forecast.json?key=9e86c689d13c42c193f173940231704&q=" + latitude + "," + longitude + "&days=2";
//                url = "http://api.weatherapi.com/v1/forecast.json?key=9e86c689d13c42c193f173940231704&q=18.4926,74.0255&days=2";
                new WeatherApiTask().execute(url);
            }
            else {
                url = "http://api.weatherapi.com/v1/forecast.json?key=9e86c689d13c42c193f173940231704&q=18.4926,74.0255&days=2";
                new WeatherApiTask().execute(url);
            }
        });

    }

}



