package com.example.homify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sensor1Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Button btnRefresh;
    private Button btnTemp;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://homify-is07-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference dataReference = database.getReference("data/"+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString().trim());

    private LineChart mChart;
    private ArrayList<Entry> yValues;
    private ArrayList<String> tempValues;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("OnStart() avviato");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor1);

        ///////////////////////////////////////////////////////////////////////////
        //Hooks//
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);

        //Tool Bar//
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu//
        //Menu menu=navigationView.getMenu();
        //menu.findItem(R.id.nav_logout).setVisible(false);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_temperatura);

        ////////////////////////////////////////////////////////////////////////////
        btnTemp = findViewById(R.id.btnSensor1Data);

        btnRefresh=findViewById(R.id.btnSensor1Refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshGraph();
            }
        });

        mChart = (LineChart) findViewById(R.id.linechart1);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setLabelCount(4,true);
        mChart.setPadding(5,5,5,5);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setXOffset(1);
        mChart.setVisibleXRange(2,7);
        mChart.setScaleMinima(75f,1f);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setCenterAxisLabels(true);
        mChart.getAxisLeft().setGridColor(Color.parseColor("#D6D6D6"));
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getAxisLeft().setDrawAxisLine(false);
        mChart.getXAxis().setGranularityEnabled(true);
        mChart.getXAxis().setGranularity(1);
        firstSettings();


    }

    private void refreshGraph() {

        yValues=new ArrayList<>();
        tempValues = new ArrayList<>();
        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // ciclo for per prendere il database e creare gli oggetti nella lista
                for (DataSnapshot snapshotDHT : snapshot.getChildren()){
                    DHT dht = snapshotDHT.getValue(DHT.class);
                    if (dht != null) {
                        tempValues.add(dht.getTemperature().toString());
                        yValues.add(new Entry(dht.setTimeAsSeconds(), dht.getTemperature()));
                    }
                }
                // uscito dal for ha preso tutti i figli

                //dati per il grafico
                LineDataSet setTemperature=new LineDataSet(yValues, "Temperatura registrata");
                setTemperature.setDrawFilled(true);
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(Sensor1Activity.this, R.drawable.chart_background_gradient);
                    setTemperature.setFillDrawable(drawable);
                }
                else {
                    setTemperature.setFillColor(Color.parseColor("#FF9700"));
                }
                setTemperature.setMode(LineDataSet.Mode.STEPPED);
                setTemperature.setCubicIntensity(0.2f);
                setTemperature.setValueTextSize(0f);
                setTemperature.setColor(Color.parseColor("#FF9700"));
                setTemperature.setDrawVerticalHighlightIndicator(false);
                setTemperature.setLineWidth(3f);
                setTemperature.setDrawCircles(false);
                setTemperature.setDrawValues(true);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(setTemperature);
                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.getLegend().setEnabled(false);
                mChart.setDrawMarkers(false);
                mChart.moveViewToX(data.getXMax());
                mChart.getAxisLeft().removeAllLimitLines();
                mChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
                mChart.postInvalidate();
                System.out.println("Aggiornamento T");
                btnTemp.setText(tempValues.get(tempValues.size()-1) + " °C");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void firstSettings() {
        yValues = new ArrayList<>();
        tempValues = new ArrayList<>();
        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // ciclo for per prendere il database e creare gli oggetti nella lista
                for (DataSnapshot snapshotDHT : snapshot.getChildren()) {
                    DHT dht = snapshotDHT.getValue(DHT.class);
                    if (dht != null) {
                        tempValues.add(dht.getTemperature().toString());
                        yValues.add(new Entry(dht.setTimeAsSeconds(), dht.getTemperature()));
                    }
                }
                // uscito dal for ha preso tutti i figli

                //dati per il grafico
                LineDataSet setTemperature = new LineDataSet(yValues, "Temperatura registrata");
                setTemperature.setDrawFilled(true);
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(Sensor1Activity.this, R.drawable.chart_background_gradient);
                    setTemperature.setFillDrawable(drawable);
                } else {
                    setTemperature.setFillColor(Color.parseColor("#FF9700"));
                }
                setTemperature.setMode(LineDataSet.Mode.STEPPED);
                setTemperature.setCubicIntensity(0.2f);
                setTemperature.setValueTextSize(0f);
                mChart.getAxisLeft().removeAllLimitLines();
                setTemperature.setColor(Color.parseColor("#FF9700"));
                setTemperature.setDrawVerticalHighlightIndicator(false);
                setTemperature.setLineWidth(3f);
                setTemperature.setDrawCircles(false);
                setTemperature.setDrawValues(false);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(setTemperature);
                LineData data = new LineData(dataSets);
                data.setDrawValues(true);
                mChart.setData(data);
                mChart.getLegend().setEnabled(false);
                mChart.setDrawMarkers(false);
                mChart.moveViewToX(data.getXMax());
                mChart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
                mChart.invalidate();
                System.out.println("Primo caricamento");
                btnTemp.setText(tempValues.get(tempValues.size() - 1) + " °C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intentsveglia = new Intent(Sensor1Activity.this, AlarmClockActivity.class);
        Intent intentmeteo = new Intent(Sensor1Activity.this, WeatherActivity.class);
        Intent intenttemperatura = new Intent(Sensor1Activity.this, Sensor1Activity.class);
        Intent intentUmidita = new Intent(Sensor1Activity.this, Sensor2Activity.class);
        Intent intentHome = new Intent(Sensor1Activity.this, UserHomeActivity.class);
        Intent intentProfilo = new Intent(Sensor1Activity.this, ProfileActivity.class);
        Intent intentLogOut = new Intent(Sensor1Activity.this, MainActivity.class);
        Intent intentImpostazioni = new Intent(Sensor1Activity.this, SettingsActivity.class);
        Intent intentGrafici = new Intent(Sensor1Activity.this, GraphsActivity.class);

        if (menuItem.getItemId() == R.id.nav_home) {
            startActivity(intentHome);
        } else if (menuItem.getItemId() == R.id.nav_sveglia) {
            startActivity(intentsveglia);
        } else if (menuItem.getItemId() == R.id.nav_meteo) {
            startActivity(intentmeteo);
        } else if (menuItem.getItemId() == R.id.nav_temperatura) {
            startActivity(intenttemperatura);
        } else if (menuItem.getItemId() == R.id.nav_umidita) {
            startActivity(intentUmidita);
        } else if (menuItem.getItemId() == R.id.nav_profile) {
            startActivity(intentProfilo);
        } else if (menuItem.getItemId() == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(intentLogOut);
        } else if (menuItem.getItemId() == R.id.nav_impostazioni) {
            startActivity(intentImpostazioni);
        } else if (menuItem.getItemId() == R.id.nav_grafici) {
            startActivity(intentGrafici);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}