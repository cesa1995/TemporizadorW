package com.example.cesar.temporizadorw;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cesar.temporizadorw.tabs.humedad;
import com.example.cesar.temporizadorw.tabs.temperatura;
import com.example.cesar.temporizadorw.tabs.temporizador;

public class show extends AppCompatActivity implements comunication {

    ToggleButton I,II,III,IV;

    TextView relayName;

    int  fragmentposition;

    PagerAdapter adapter;
    TabLayout tabLayout;
    Log LOG_TAG;

    int relay2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        String ssid = getWifiName(getApplicationContext());
        System.out.println("el ssid es "+ssid);
        if(ssid.contains("TEMP")) {

            I = findViewById(R.id.I);
            II = findViewById(R.id.II);
            III = findViewById(R.id.III);
            IV = findViewById(R.id.IV);
            I.setChecked(true);
            II.setChecked(false);
            III.setChecked(false);
            IV.setChecked(false);
            relayName = findViewById(R.id.relayName);

            adapter = new PagerAdapter(getSupportFragmentManager());
            ViewPager viewPager = (ViewPager) findViewById(R.id.container);
            viewPager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

            I.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        selec(II);
                    } else {
                        selec(I);
                    }
                }
            });

            II.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        selec(III);
                    } else {
                        selec(II);
                    }
                }
            });

            III.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        selec(IV);
                    } else {
                        selec(III);
                    }
                }
            });

            IV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        selec(I);
                    } else {
                        selec(IV);
                    }
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            finish();
        }

    }

    @Override
    public void sendata(int fragment, int relay) {
        fragmentposition = fragment;
        switch (relay){
            case 0:
                selec(I);
                break;
            case 1:
                selec(II);
                break;
            case 2:
                selec(III);
                break;
            case 3:
                selec(IV);
                break;
        }
    }


    public void selec(ToggleButton relay){
        I.setChecked(false);
        II.setChecked(false);
        III.setChecked(false);
        IV.setChecked(false);
        if (relay.equals(I)){
            II.setChecked(false);
            III.setChecked(false);
            IV.setChecked(false);
            I.setChecked(true);
            relay2 = 0;
            relayName.setText("uno");
        }else if (relay.equals(II)){
            I.setChecked(false);
            III.setChecked(false);
            IV.setChecked(false);
            II.setChecked(true);
            relay2 = 1;
            relayName.setText("dos");
        }else{
            if (relay.equals(III)){
                I.setChecked(false);
                II.setChecked(false);
                IV.setChecked(false);
                III.setChecked(true);
                relay2 = 2;
                relayName.setText("tres");
            }else if (relay.equals(IV)){
                II.setChecked(false);
                III.setChecked(false);
                I.setChecked(false);
                IV.setChecked(true);
                relay2 = 3;
                relayName.setText("cuatro");
            }
        }

        switch (fragmentposition){
            case 0:
                temporizador temporizador = (temporizador)adapter.getFragment(0);
                if (temporizador != null){
                    temporizador.cambiarrelay(relay2);
                }else {
                    Log.i(String.valueOf(LOG_TAG), "Fragment is not initialized 1");
                }
                break;
            case 1:
                humedad humedad = (humedad) adapter.getFragment(1);
                if (humedad != null){
                    humedad.cambiarrelay(relay2);
                }else {
                    Log.i(String.valueOf(LOG_TAG), "Fragment is not initialized 2");
                }
                break;
            case 2:
                temperatura temperatura = (temperatura) adapter.getFragment(2);
                if (temperatura != null){
                    temperatura.cambiarrelay(relay2);
                }else {
                    Log.i(String.valueOf(LOG_TAG), "Fragment is not initialized 3");
                }
                break;

        }
    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
            finish();
        }else{
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
            return " ";
        }
}
