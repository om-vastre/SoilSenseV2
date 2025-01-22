package com.example.soilsense;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class HomeFragment extends Fragment {

    CircularProgressIndicator pbEC, pbNPK, pbHum, pbPWet, pbSMoist, pbpH;
    TextView tvEC, tvNPK, tvHumidity, tvPWet, tvMoist, tvpH;
    String userName, userNo;
    View.OnClickListener cardClickListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle args = getArguments();
        userName = args.getString("userName");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/users/"+userName);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userNo = dataSnapshot.getValue(String.class);
                Log.d("Firebase", "Value: " + userNo);
                if(getActivity()!=null){
                    getActivity().runOnUiThread(() -> getDashboard(userNo));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(getActivity(), "Failed to find user in Users DB!.", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        View.OnClickListener cardClickListener = view1 -> {
            int id = view1.getId();
            switch (id) {
                case R.id.cardView1:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "ec").putExtra("userNo", userNo));
                    break;
                case R.id.cardView2:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "npk").putExtra("userNo", userNo));
                    break;
                case R.id.cardView3:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "ehumi").putExtra("userNo", userNo));
                    break;
                case R.id.cardView4:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "pwet").putExtra("userNo", userNo));
                    break;
                case R.id.cardView5:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "shumi").putExtra("userNo", userNo));
                    break;
                case R.id.cardView6:
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("sname", "ph").putExtra("userNo", userNo));
                    break;


                case R.id.cvSensor:
                    startActivity( new Intent(getActivity(), SensorsActivity.class).putExtra("userNo", userNo) );
                    break;
                case R.id.cvSpray:
                    startActivity( new Intent(getActivity(), SprayActivity.class).putExtra("userNo", userNo) );
                    break;
                case R.id.cvAnalysis:
                    startActivity( new Intent(getActivity(), AnalysisActivity.class).putExtra("userNo", userNo) );
                    break;
                case R.id.home_cvNPK:
                    startActivity( new Intent(getActivity(), NPKActivity.class).putExtra("userNo", userNo) );
                    break;
                case R.id.cvSwitch:
                    startActivity( new Intent(getActivity(), AutoActivity.class).putExtra("userNo", userNo) );
                    break;
                case R.id.cvAlert:
                    Toast.makeText(getActivity(), "Alert Temp", Toast.LENGTH_SHORT).show();
                    break;
            }
        };

        pbEC = view.findViewById(R.id.circularProgressIndicator1);
        pbNPK = view.findViewById(R.id.circularProgressIndicator2);
        pbHum = view.findViewById(R.id.circularProgressIndicator3);
        pbPWet = view.findViewById(R.id.circularProgressIndicator4);
        pbSMoist = view.findViewById(R.id.circularProgressIndicator5);
        pbpH = view.findViewById(R.id.circularProgressIndicator6);

        view.findViewById(R.id.cardView1).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cardView2).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cardView3).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cardView4).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cardView5).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cardView6).setOnClickListener(cardClickListener);

        view.findViewById(R.id.cvSensor).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cvSpray).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cvAnalysis).setOnClickListener(cardClickListener);
        view.findViewById(R.id.home_cvNPK).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cvSwitch).setOnClickListener(cardClickListener);
        view.findViewById(R.id.cvAlert).setOnClickListener(cardClickListener);


        return view;
    }

    private void getDashboard(String userNo){
        Log.d("UserNo", "UserNo"+userNo);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo+"/sensors");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the values of the dashboard progress bars
                    String ecValue = dataSnapshot.child("ec").getValue(String.class);
                    String npkValue = dataSnapshot.child("npk").getValue(String.class);
                    String ehumiValue = dataSnapshot.child("ehumi").getValue(String.class);
                    String pwetValue = dataSnapshot.child("pwet").getValue(String.class);
                    String shumiValue = dataSnapshot.child("shumi").getValue(String.class);
                    String phValue = dataSnapshot.child("ph").getValue(String.class);

                    Log.d("Firebase", "ecValue: " + ecValue);
                    Log.d("Firebase", "npkValue: " + npkValue);
                    Log.d("Firebase", "pwetValue: " + pwetValue);
                    Log.d("Firebase", "ehumiValue: " + ehumiValue);


                    requireActivity().runOnUiThread(() -> setDashboard(new String[]{ecValue, npkValue, ehumiValue, pwetValue, shumiValue, phValue}));

                } else {
                    Toast.makeText(getActivity(), "Dashoard data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(getActivity(), "Failed to read dashboard value!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void setDashboard(String[] progressVals){

        // Replacing "nan" with "0"
        Arrays.setAll(progressVals, i -> progressVals[i].replaceAll("nan", "0"));

        pbEC = getView().findViewById(R.id.circularProgressIndicator1);
        tvEC = getView().findViewById(R.id.tvEC);
        pbNPK = getView().findViewById(R.id.circularProgressIndicator2);
        tvNPK = getView().findViewById(R.id.tvNPK);
        pbHum = getView().findViewById(R.id.circularProgressIndicator3);
        tvHumidity = getView().findViewById(R.id.tvHumidity);
        pbPWet = getView().findViewById(R.id.circularProgressIndicator4);
        tvPWet = getView().findViewById(R.id.tvPWetness);
        pbSMoist = getView().findViewById(R.id.circularProgressIndicator5);
        tvMoist = getView().findViewById(R.id.tvMoisture);
        pbpH = getView().findViewById(R.id.circularProgressIndicator6);
        tvpH = getView().findViewById(R.id.tvpH);


        pbEC.setProgress( (int) Double.parseDouble(progressVals[0])*10 );
        if ( Double.parseDouble(progressVals[0])> 2.5){
            pbEC.setIndicatorColor(Color.parseColor("#FF0000"));
        } else{
            pbEC.setIndicatorColor(Color.parseColor("#26D192"));
        }
        tvEC.setText(progressVals[0]);


        if(progressVals[1].equals("0")){
            progressVals[1]="0-0-0";
        }
        double temp = Arrays.stream(progressVals[1].split("-"))
                .mapToInt(Integer::parseInt)
                .sum();
//        temp /= 30;
        Log.d("temp", "Temp = "+temp);
        pbNPK.setProgress((int) temp/30);
        if ( temp > 1750.00 && temp < 2050.00){
            pbNPK.setIndicatorColor(Color.parseColor("#26D192"));
        } else{
            pbNPK.setIndicatorColor(Color.parseColor("#FF0000"));
        }
        tvNPK.setText(Arrays.stream(progressVals[1].split("-")).map(s -> String.valueOf(Integer.parseInt(s)/100)).collect(Collectors.joining(" : ")));


        pbHum.setProgress((int) Double.parseDouble(progressVals[2]));
        if ( Double.parseDouble(progressVals[2])> 70.0){
            pbHum.setIndicatorColor(Color.parseColor("#FF0000"));
        } else{
            pbHum.setIndicatorColor(Color.parseColor("#26D192"));
        }
        tvHumidity.setText(String.format("%s%%", progressVals[2]));


        pbPWet.setProgress( (int) Double.parseDouble(progressVals[3]));
        if ( Double.parseDouble(progressVals[3])< 35.0){
            pbPWet.setIndicatorColor(Color.parseColor("#FF0000"));
        } else{
            pbPWet.setIndicatorColor(Color.parseColor("#26D192"));
        }
        tvPWet.setText(String.format("%s%%", progressVals[3]));


        pbSMoist.setProgress((int) Double.parseDouble(progressVals[4]));
        if ( Double.parseDouble(progressVals[4])< 30.0){
            pbSMoist.setIndicatorColor(Color.parseColor("#FF0000"));
        } else{
            pbSMoist.setIndicatorColor(Color.parseColor("#26D192"));
        }
        tvMoist.setText(String.format("%s%%", progressVals[4]));


        pbpH.setProgress((int) (Double.parseDouble(progressVals[5])*100.0/14));
        if(Double.parseDouble(progressVals[5])< 6.5 || Double.parseDouble(progressVals[5])>7.5){
            pbpH.setIndicatorColor(Color.parseColor("#FF0000"));
        } else{
            pbpH.setIndicatorColor(Color.parseColor("#26D192"));
        }
        tvpH.setText(progressVals[5]);

    }


}
