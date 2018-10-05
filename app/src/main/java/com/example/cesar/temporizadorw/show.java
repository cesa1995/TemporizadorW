package com.example.cesar.temporizadorw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cesar.temporizadorw.tabs.humedad;
import com.example.cesar.temporizadorw.tabs.temperatura;
import com.example.cesar.temporizadorw.tabs.temporizador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class show extends AppCompatActivity{

    ToggleButton I,II,III,IV;
    TextView relayName;
    int  fragmentposition;
    LinearLayoutCompat relayes;
    PagerAdapter adapter;
    TabLayout tabLayout;
    Log LOG_TAG;
    int relay2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
            I = findViewById(R.id.I);
            II = findViewById(R.id.II);
            III = findViewById(R.id.III);
            IV = findViewById(R.id.IV);
            I.setChecked(true);
            II.setChecked(false);
            III.setChecked(false);
            IV.setChecked(false);
            relayName = findViewById(R.id.relayName);
            fragmentposition = 0;
            relayes = findViewById(R.id.relayes);

            adapter = new PagerAdapter(getSupportFragmentManager());
            ViewPager viewPager = (ViewPager) findViewById(R.id.container);
            viewPager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (tabLayout.getTabAt(0).isSelected()){
                        relayes.setVisibility(View.VISIBLE);
                        Toast.makeText(show.this,"Tiempo",Toast.LENGTH_SHORT).show();
                        fragmentposition = 0;
                        Log.i(String.valueOf(LOG_TAG), String.valueOf(fragmentposition));
                        selec(I);
                    }else if (tabLayout.getTabAt(1).isSelected()){
                        relayes.setVisibility(View.VISIBLE);
                        Toast.makeText(show.this, "Humedad", Toast.LENGTH_SHORT).show();
                        fragmentposition = 1;
                        Log.i(String.valueOf(LOG_TAG), String.valueOf(fragmentposition));
                        selec(I);
                    }else if (tabLayout.getTabAt(2).isSelected()){
                        relayes.setVisibility(View.VISIBLE);
                        Toast.makeText(show.this, "Temperarura", Toast.LENGTH_SHORT).show();
                        fragmentposition = 2;
                        Log.i(String.valueOf(LOG_TAG), String.valueOf(fragmentposition));
                        selec(I);
                    }else {
                        relayes.setVisibility(View.GONE);
                        Toast.makeText(show.this, "Configuracion", Toast.LENGTH_SHORT).show();
                        fragmentposition = 3;
                        Log.i(String.valueOf(LOG_TAG), String.valueOf(fragmentposition));
                        selec(I);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
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


    }

    public void selec(ToggleButton relay){
        Log.i(String.valueOf(LOG_TAG), String.valueOf(fragmentposition));
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
}
