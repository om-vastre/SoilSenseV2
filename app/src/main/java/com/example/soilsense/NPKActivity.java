package com.example.soilsense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class NPKActivity extends AppCompatActivity {

    String userNo;
    Float n, p, k;
    TextView urea, ssp, mop, sop, dap, potash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npkactivity);
        userNo = getIntent().getExtras().getString("userNo");

        PieChart NPie = findViewById(R.id.NPie_Chart);
        PieChart PPie = findViewById(R.id.PPie_Chart);
        PieChart KPie = findViewById(R.id.KPie_Chart);

        urea = findViewById(R.id.tvUrea);
        ssp = findViewById(R.id.tvSSP);
        mop = findViewById(R.id.tvMOP);
        sop = findViewById(R.id.tvSOP);
        dap = findViewById(R.id.tvDAP);
        potash = findViewById(R.id.tvPota);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(userNo+"/npkman");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    n = Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("n").getValue(String.class)));
                    p = Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("p").getValue(String.class)));
                    k = Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("k").getValue(String.class)));

                    NPKActivity.this.runOnUiThread(() -> new DrawPieTask(NPie, n/100, (1000 - n)/100 ).execute() );
                    NPKActivity.this.runOnUiThread(() -> new DrawPieTask(PPie, p/100, (1000 - n)/100 ).execute() );
                    NPKActivity.this.runOnUiThread(() -> new DrawPieTask(KPie, k/100, (1000 - n)/100 ).execute() );

                } else {
                    Toast.makeText(NPKActivity.this, "NPK data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available"+userNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NPKActivity.this, "Failed to read NPK value!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });


        FirebaseDatabase.getInstance().getReference(userNo+"/npkman/suggetion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String urea, ssp, mop, sop, dap, potash;
                    urea = dataSnapshot.child("urea").getValue(String.class);
                    ssp = dataSnapshot.child("ssp").getValue(String.class);
                    mop = dataSnapshot.child("mop").getValue(String.class);
                    sop = dataSnapshot.child("sop").getValue(String.class);
                    dap = dataSnapshot.child("dap").getValue(String.class);
                    potash = dataSnapshot.child("potash").getValue(String.class);

                    NPKActivity.this.runOnUiThread(() -> setSuggestion( new String[]{urea, ssp, mop, sop, dap, potash} ) );

                } else {
                    Toast.makeText(NPKActivity.this, "NPK data not found!", Toast.LENGTH_LONG ).show();
                    Log.d("Firebase", "No data available"+userNo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NPKActivity.this, "Failed to read NPK value!", Toast.LENGTH_LONG ).show();
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
        findViewById(R.id.npk_backButton).setOnClickListener(v -> NPKActivity.super.onBackPressed());
    }

    private class DrawPieTask extends AsyncTask<Void, Void, Void> {
        private final PieChart pie;
        private final float available;
        private final float req;

        public DrawPieTask(PieChart pie, float available, float req) {
            this.pie = pie;
            this.available = available;
            this.req = req;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pie.setNoDataText("Drawing Pie Chart...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            drawPie(pie, available, req);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pie.invalidate();
            pie.animateY(1400, Easing.EaseInOutQuad);
        }
    }

    public void drawPie(PieChart Pie, float available, float req) {
        Pie.setDrawHoleEnabled(true);
        Pie.setUsePercentValues(true);
        Pie.setEntryLabelTextSize(8);
        Pie.setEntryLabelColor(Color.BLACK);
//            NPie.setCenterText("Spending by Category");
//            NPie.setCenterTextSize(24);
        Pie.getDescription().setEnabled(false);

        Legend l = Pie.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);


        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(available, "Available"));
        entries.add(new PieEntry(req, "Out Off"));
//        entries.add(new PieEntry(req, "Out Off"));


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(Pie));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        Pie.setData(data);
    }

    private void setSuggestion(String[] sugges){
        urea.setText(String.format("%s Kg/ha", sugges[0]));
        ssp.setText(String.format("%s Kg/ha", sugges[1]));
        mop.setText(String.format("%s Kg/ha", sugges[2]));
        sop.setText(String.format("%s Kg/ha", sugges[3]));
        dap.setText(String.format("%s Kg/ha", sugges[4]));
        potash.setText(String.format("%s Kg/ha", sugges[5]));
    }
}