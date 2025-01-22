package com.example.soilsense;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class TimeBottomSheetFragment extends BottomSheetDialogFragment {

    TimePicker tpStart, tpStop;
    Button btnSetTime, btnStop;
    long startTime, stopTime;
    String userNo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_bottom_sheet, container, false);


        Bundle args = getArguments();
        userNo = args.getString("userNo");
        btnSetTime = view.findViewById(R.id.BtnSetMotorTime);
        btnStop = view.findViewById(R.id.btnStopMotorTime);
        tpStart = view.findViewById(R.id.tpStart);
        tpStop = view.findViewById(R.id.tpStop);

        tpStart.setIs24HourView(true);
        tpStop.setIs24HourView(true);

//        int startHour = tpStart.getHour();
//        int startMinute = tpStart.getMinute();
//        long startTime = TimeUnit.HOURS.toMillis(startHour) + TimeUnit.MINUTES.toMillis(startMinute);
//        int stopHour = tpStop.getHour();
//        int stopMinute = tpStop.getMinute();
//        long stopTime = TimeUnit.HOURS.toMillis(stopHour) + TimeUnit.MINUTES.toMillis(stopMinute);



        Intent intent = new Intent(getActivity(), MotorService.class);
        intent.putExtra("userNo", userNo);

        btnSetTime.setOnClickListener(v -> {
            int hour, minute;
            Calendar calendar;
            hour = tpStart.getHour();
            minute = tpStart.getMinute();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            startTime = calendar.getTimeInMillis();

            hour = tpStop.getHour();
            minute = tpStop.getMinute();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            stopTime = calendar.getTimeInMillis();

            intent.putExtra("start_time", startTime);
            intent.putExtra("stop_time", stopTime);

            Log.d("TimePicker", startTime+" : "+stopTime);

            getActivity().startService(intent);
            dismiss();
            Toast.makeText(getActivity(), "Start : "+tpStart.getHour()+" : "+tpStart.getMinute()+"\nStop : "+tpStop.getHour()+" : "+tpStop.getMinute(), Toast.LENGTH_SHORT).show();
            Log.d("Motor Service", "Started : "+"Start : "+tpStart.getHour()+" : "+tpStart.getMinute()+"\nStop : "+tpStop.getHour()+" : "+tpStop.getMinute());
        });

        btnStop.setOnClickListener(v -> {

            int hour, minute;
            Calendar calendar;
            hour = tpStart.getHour();
            minute = tpStart.getMinute();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            startTime = calendar.getTimeInMillis();

            hour = tpStop.getHour();
            minute = tpStop.getMinute();
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            stopTime = calendar.getTimeInMillis();

            intent.putExtra("start_time", startTime);
            intent.putExtra("stop_time", stopTime);

            getActivity().stopService(intent);
            dismiss();
            Toast.makeText(getActivity(), "Stop Service", Toast.LENGTH_SHORT).show();
            Log.d("Motor Service", "Stopped");
        });

        return view;
    }

}

