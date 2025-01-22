package com.example.soilsense;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LiterFragmentBottomSheet extends BottomSheetDialogFragment {

    String userNo, tem;
    private DatabaseReference mDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liter_bottom_sheet, container, false);

        Bundle args = getArguments();
        userNo = args.getString("userNo");

        Button btnSetWater = view.findViewById(R.id.BtnSetMotorLiter);
        EditText waterLiter = view.findViewById(R.id.etWaterLiter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(userNo+"/auto/motor/liter");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requireActivity().runOnUiThread(() -> waterLiter.setText(dataSnapshot.getValue(String.class)));
                } else {
                    Toast.makeText(getActivity(), "Water Liter data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors
                Toast.makeText(getActivity(), "Failed to read Water Liter data!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        btnSetWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tem = String.valueOf( waterLiter.getText() );
                mDatabaseReference.setValue( tem );
                dismiss();
                Toast.makeText(getActivity(), "Water limit applied", Toast.LENGTH_SHORT ).show();
            }
        });


        return view;
    }
}

