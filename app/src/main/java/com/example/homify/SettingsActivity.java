package com.example.homify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private Button btnConfigura;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Hooks//
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //Tool Bar//
        setSupportActionBar(toolbar);

        // Navigation Drawer Menu//
        Menu menu = navigationView.getMenu();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_impostazioni);

        /** Connessione con il nodo sensore:
         * Poiché la gestione del Bluetooth è molto articolata e degrada le prestazioni del
         * microcontrollore, ho deciso di utilizzare nuovamente la rete Wi-Fi per comunicare
         * al nodo tre parametri: nome della rete (SSID), password della rete e user ID.
         *
         * Funzionamento:
         * Durante la fase di prima configurazione il nodo genera una rete Wi-Fi proprietaria
         * e hosta su una porta (192.168.4.1) una piccola pagina HTML che ho scritto personalmente
         * in cui inserire i dati richiesti da sottomettere tramite un bottone.
         * Il microcontrollore salva nella memoria EEPROM questi dati, verifica che possa
         * connettersi correttamente alla rete di casa e una volta connesso smette di funzionare
         * in AP-mode (Access Point), ossia spegne il Wi-Fi proprietario.
         *
         * Vantaggi:
         *  -> La connessione con il nodo sensore risulta molto più stabile ed affidabile
         *  -> Non è necessario trovarsi nelle immediate vicinanze del nodo (è invece obbligatorio
         *  per la connessione Bluetooth), in quanto la rete Wi-Fi ha una copertura maggiore
         *  -> Il firmware del microcontrollore è molto più snello e stabile
         *
         * Svantaggi:
         *  -> Nessuno, anzi, questo sistema è utilizzato da tutti i prodotti domotici compresi
         *  assistenti vocali (Google Home ad esempio) e lampadine smart. */

        btnConfigura = findViewById(R.id.btnConfigura);
        btnConfigura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Manda l'utente nelle impostazioni per connettersi alla rete Wi-Fi del nodo
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                //Chiede all'utente di connettersi alla rete Wi-Fi del nodo
                Toast.makeText(SettingsActivity.this, "Connettiti alla rete del nodo", Toast.LENGTH_LONG).show();

                // Flag per controllare il ritorno all'applicazione
                flag = true;

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ritorno all'applicazione
        if (flag) {

            // Passa a WebViewActivity per accedere ala pagina di configurazione del nodo
            startActivity(new Intent(SettingsActivity.this, WebViewActivity.class));
            finish();
        }
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
        Intent intentsveglia = new Intent(SettingsActivity.this, AlarmClockActivity.class);
        Intent intentmeteo = new Intent(SettingsActivity.this, WeatherActivity.class);
        Intent intenttemperatura = new Intent(SettingsActivity.this, Sensor1Activity.class);
        Intent intentUmidita = new Intent(SettingsActivity.this, Sensor2Activity.class);
        Intent intentHome = new Intent(SettingsActivity.this, UserHomeActivity.class);
        Intent intentProfilo = new Intent(SettingsActivity.this, ProfileActivity.class);
        Intent intentLogOut = new Intent(SettingsActivity.this, MainActivity.class);
        Intent intentImpostazioni = new Intent(SettingsActivity.this, SettingsActivity.class);
        Intent intentGrafici = new Intent(SettingsActivity.this, GraphsActivity.class);

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