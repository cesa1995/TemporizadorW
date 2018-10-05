package com.example.cesar.temporizadorw.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cesar.temporizadorw.R;
import com.example.cesar.temporizadorw.conexion;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class humedad extends Fragment {

    TextView humedadvalue,huminicial,humfinal,ontime,oftime;
    Switch humedadswi;
    ImageButton huminicialbtn,humfinalbtn,ontimebtn,oftimebtn;
    NumberPicker hora, minutos, segundos, humedad;
    int relay, hh, ss, mm, bon, hum;
    ProgressBar cargando;
    ArrayList<HashMap<String, String>> relayListhum;
    String ONe, OFFe, HINIe, HFINe, urlsave;
    SharedPreferences data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_humedad, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v){
        humedadswi = v.findViewById(R.id.humedadswi);
        humedadvalue = v.findViewById(R.id.humedadActual);
        huminicial = v.findViewById(R.id.huminicial);
        humfinal = v.findViewById(R.id.humfinal);
        ontime = v.findViewById(R.id.ontime);
        oftime = v.findViewById(R.id.oftime);
        huminicialbtn = v.findViewById(R.id.huminicialbtn);
        humfinalbtn = v.findViewById(R.id.humfinalbtn);
        ontimebtn = v.findViewById(R.id.ontimebtn);
        oftimebtn = v.findViewById(R.id.oftimebtn);
        relayListhum = new ArrayList<>();
        relay = 0;
        cargando = v.findViewById(R.id.progressBar);
        data = this.getActivity().getSharedPreferences("datosDevice",Context.MODE_PRIVATE);

        new Gettimes().execute();

        huminicialbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon=boton(1,relay);
                dialoghum(bon,hum);
                selec();
            }
        });

        humfinalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon=boton(2,relay);
                dialoghum(bon,hum);
                selec();
            }
        });

        ontimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon=boton(3,relay);
                dialog(bon,hh,mm,ss);
                selec();

            }
        });

        oftimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon=boton(4,relay);
                dialog(bon,hh,mm,ss);
                selec();
            }
        });

    }


    public void dialoghum(final int RR, int hum){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.picker_hum_temp,null);
        builder.setView(d);
        builder.setTitle("Selecciones humedad:");
        builder.setMessage("%");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hum = String.valueOf(humedad.getValue());
                guardar(RR,null,null,null, hum);
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        humedad = d.findViewById(R.id.humtemp);

        humedad.setMaxValue(100);
        humedad.setMinValue(0);

        humedad.setValue(hum);

        builder.create();
        builder.show();
    }

    public void dialog(final int RR, int hh, int mm, int ss){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View f = inflater.inflate(R.layout.numberpicker,null);
        builder.setView(f);
        builder.setTitle("Selecciones el Tiempo:");
        builder.setMessage("HH/MM/SS");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hh = String.valueOf(hora.getValue());
                String mm = String.valueOf(minutos.getValue());
                String ss = String.valueOf(segundos.getValue());
                guardar(RR,hh,mm,ss,null);
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        hora = f.findViewById(R.id.hora);
        minutos = f.findViewById(R.id.minutos);
        segundos = f.findViewById(R.id.segundos);

        hora.setMaxValue(23);
        hora.setMinValue(0);
        minutos.setMaxValue(59);
        minutos.setMinValue(0);
        segundos.setMaxValue(59);
        segundos.setMinValue(0);
        hora.setValue(hh);
        minutos.setValue(mm);
        segundos.setValue(ss);

        builder.create();
        builder.show();
    }

    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!manager.isWifiEnabled()) {
            manager.setWifiEnabled(true);
            getActivity().finish();
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

    public int boton(int boton, int relay){
        int RR = 0;
        switch (relay){
            case 0:
                switch (boton) {
                    case 1:
                        hum=Integer.parseInt(relayListhum.get(0).get("humINI"));
                        RR = 1;
                        break;
                    case 2:
                        hum=Integer.parseInt(relayListhum.get(0).get("humFIN"));
                        RR = 2;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListhum.get(0).get("humONhh"));
                        mm=Integer.parseInt(relayListhum.get(0).get("humONmm"));
                        ss=Integer.parseInt(relayListhum.get(0).get("humONss"));
                        RR = 17;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListhum.get(0).get("humOFFhh"));
                        mm=Integer.parseInt(relayListhum.get(0).get("humOFFmm"));
                        ss=Integer.parseInt(relayListhum.get(0).get("humOFFss"));
                        RR = 18;
                        break;
                }
                break;
            case 1:
                switch (boton) {
                    case 1:
                        hum=Integer.parseInt(relayListhum.get(1).get("humINI"));
                        RR = 3;
                        break;
                    case 2:
                        hum=Integer.parseInt(relayListhum.get(1).get("humFIN"));
                        RR = 4;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListhum.get(1).get("humONhh"));
                        mm=Integer.parseInt(relayListhum.get(1).get("humONmm"));
                        ss=Integer.parseInt(relayListhum.get(1).get("humONss"));
                        RR = 19;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListhum.get(1).get("humOFFhh"));
                        mm=Integer.parseInt(relayListhum.get(1).get("humOFFmm"));
                        ss=Integer.parseInt(relayListhum.get(1).get("humOFFss"));
                        RR = 20;
                        break;
                }
                break;
            case 2:
                switch (boton) {
                    case 1:
                        hum=Integer.parseInt(relayListhum.get(2).get("humINI"));
                        RR = 5;
                        break;
                    case 2:
                        hum=Integer.parseInt(relayListhum.get(2).get("humFIN"));
                        RR = 6;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListhum.get(2).get("humONhh"));
                        mm=Integer.parseInt(relayListhum.get(2).get("humONmm"));
                        ss=Integer.parseInt(relayListhum.get(2).get("humONss"));
                        RR = 21;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListhum.get(2).get("humOFFhh"));
                        mm=Integer.parseInt(relayListhum.get(2).get("humOFFmm"));
                        ss=Integer.parseInt(relayListhum.get(2).get("humOFFss"));
                        RR = 22;
                        break;
                }
                break;
            case 3:
                switch (boton) {
                    case 1:
                        hum=Integer.parseInt(relayListhum.get(3).get("humINI"));
                        RR = 7;
                        break;
                    case 2:
                        hum=Integer.parseInt(relayListhum.get(3).get("humFIN"));
                        RR = 8;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListhum.get(3).get("humONhh"));
                        mm=Integer.parseInt(relayListhum.get(3).get("humONmm"));
                        ss=Integer.parseInt(relayListhum.get(3).get("humONss"));
                        RR = 23;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListhum.get(3).get("humOFFhh"));
                        mm=Integer.parseInt(relayListhum.get(3).get("humOFFmm"));
                        ss=Integer.parseInt(relayListhum.get(3).get("humOFFss"));
                        RR = 24;
                        break;
                }
                break;
        }
        return RR;
    }

    public void guardar(int RR, String hhh, String mmm, String sss, String humm){
        String ssid = getWifiName(getContext());
        SharedPreferences.Editor editor = data.edit();
        if (ssid.contains("TEMP")) {
            String RRR = String.valueOf(RR);
            urlsave ="http://192.168.4.1/config?ssid=stiotca&pass=1234567";
            //guardar solo datos de humedad
            if (RR < 9 && humm != null){
                switch (RR){
                    case 1:
                        relayListhum.get(0).put("humINI",humm);
                        editor.putString("humINI0",humm);
                        break;
                    case 2:
                        relayListhum.get(0).put("humFIN",humm);
                        editor.putString("humFIN0",humm);
                        break;
                    case 3:
                        relayListhum.get(1).put("humINI",humm);
                        editor.putString("humINI1",humm);
                        break;
                    case 4:
                        relayListhum.get(1).put("humFIN",humm);
                        editor.putString("humFIN1",humm);
                        break;
                    case 5:
                        relayListhum.get(2).put("humINI",humm);
                        editor.putString("humINI2",humm);
                        break;
                    case 6:
                        relayListhum.get(2).put("humFIN",humm);
                        editor.putString("humFIN2",humm);
                        break;
                    case 7:
                        relayListhum.get(3).put("humINI",humm);
                        editor.putString("humINI3",humm);
                        break;
                    case 8:
                        relayListhum.get(3).put("humFIN",humm);
                        editor.putString("humFIN3",humm);
                        break;
                }

                urlsave = urlsave+"&tconfig=0&humTemp=" +humm+ "&R=" + RRR;
                System.out.println(urlsave);

            }else {
                switch (RR) {
                    case 17:
                        relayListhum.get(0).put("humONhh", hhh);
                        relayListhum.get(0).put("humONmm", mmm);
                        relayListhum.get(0).put("humONss", sss);
                        editor.putString("humONhh0",String.valueOf(hhh));
                        editor.putString("humONmm0",String.valueOf(mmm));
                        editor.putString("humONss0",String.valueOf(sss));
                        break;
                    case 18:
                        relayListhum.get(0).put("humOFFhh", hhh);
                        relayListhum.get(0).put("humOFFmm", mmm);
                        relayListhum.get(0).put("humOFFss", sss);
                        editor.putString("humOFFhh0",String.valueOf(hhh));
                        editor.putString("humOFFmm0",String.valueOf(mmm));
                        editor.putString("humOFFss0",String.valueOf(sss));
                        break;
                    case 19:
                        relayListhum.get(1).put("humONhh", hhh);
                        relayListhum.get(1).put("humONmm", mmm);
                        relayListhum.get(1).put("humONss", sss);
                        editor.putString("humONhh1",String.valueOf(hhh));
                        editor.putString("humONmm1",String.valueOf(mmm));
                        editor.putString("humONss1",String.valueOf(sss));
                        break;
                    case 20:
                        relayListhum.get(1).put("humOFFhh", hhh);
                        relayListhum.get(1).put("humOFFmm", mmm);
                        relayListhum.get(1).put("humOFFss", sss);
                        editor.putString("humOFFhh1",String.valueOf(hhh));
                        editor.putString("humOFFmm1",String.valueOf(mmm));
                        editor.putString("humOFFss1",String.valueOf(sss));
                        break;
                    case 21:
                        relayListhum.get(2).put("humONhh", hhh);
                        relayListhum.get(2).put("humONmm", mmm);
                        relayListhum.get(2).put("humONss", sss);
                        editor.putString("humONhh2",String.valueOf(hhh));
                        editor.putString("humONmm2",String.valueOf(mmm));
                        editor.putString("humONss2",String.valueOf(sss));
                        break;
                    case 22:
                        relayListhum.get(2).put("humOFFhh", hhh);
                        relayListhum.get(2).put("humOFFmm", mmm);
                        relayListhum.get(2).put("humOFFss", sss);
                        editor.putString("humOFFhh2",String.valueOf(hhh));
                        editor.putString("humOFFmm2",String.valueOf(mmm));
                        editor.putString("humOFFss2",String.valueOf(sss));
                        break;
                    case 23:
                        relayListhum.get(3).put("humONhh", hhh);
                        relayListhum.get(3).put("humONmm", mmm);
                        relayListhum.get(3).put("humONss", sss);
                        editor.putString("humONhh3",String.valueOf(hhh));
                        editor.putString("humONmm3",String.valueOf(mmm));
                        editor.putString("humONss3",String.valueOf(sss));
                        break;
                    case 24:
                        relayListhum.get(3).put("humOFFhh", hhh);
                        relayListhum.get(3).put("humOFFmm", mmm);
                        relayListhum.get(3).put("humOFFss", sss);
                        editor.putString("humOFFhh3",String.valueOf(hhh));
                        editor.putString("humOFFmm3",String.valueOf(mmm));
                        editor.putString("humOFFss3",String.valueOf(sss));
                        break;
                }

                urlsave = urlsave + "&tconfig=2&hh=" + hhh + "&mm=" + mmm + "&ss=" + sss + "&R=" + RRR;
                System.out.println(urlsave);
            }

            editor.apply();
            editor.commit();

            new solicitarDatos().execute(urlsave);

            urlsave = "";

            selec();
        }else{
            Toast.makeText(getContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            getActivity().finish();
        }

    }

    public void cambiarrelay(int dato){
        relay = dato;
        selec();
    }

    public class Gettimes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            cargando.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!relayListhum.isEmpty()) {
                //for (int i = 0; i < relayList.size(); i++) {
                //  relayList.remove(i);
                //}
                relayListhum.clear();
            }
            // looping through All Times
            for (int i = 0; i < 4; i++) {
                String humONhh = data.getString("humONhh"+i, "0");
                String humONmm = data.getString("humONmm"+i, "0");
                String humONss = data.getString("humONss"+i, "0");
                String humOFFhh = data.getString("humOFFhh"+i, "0");
                String humOFFmm = data.getString("humOFFmm"+i, "0");
                String humOFFss = data.getString("humOFFss"+i, "0");
                String humINI = data.getString("humINI"+i, "0");
                String humFIN = data.getString("humFIN"+i, "0");

                // tmp hash map for single Times
                HashMap<String, String> relay = new HashMap<>();

                // adding each child node to HashMap key => value
                relay.put("humONhh", humONhh);
                relay.put("humONmm", humONmm);
                relay.put("humONss", humONss);
                relay.put("humOFFhh", humOFFhh);
                relay.put("humOFFmm", humOFFmm);
                relay.put("humOFFss", humOFFss);
                relay.put("humINI", humINI);
                relay.put("humFIN", humFIN);

                // adding contact to contact list
                relayListhum.add(relay);

                System.out.println(relayListhum.toString());

                //entryArrayList= new ArrayList<>(relay.entrySet());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            selec();
            //mCallback.sendata(1, relay);
            cargando.setVisibility(View.INVISIBLE);
        }

    }


    public void selec(){

        if (relay==0 &&!relayListhum.isEmpty()){
            ONe = Integer.parseInt(relayListhum.get(0).get("humONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(0).get("humONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(0).get("humONss"));
            OFFe = Integer.parseInt(relayListhum.get(0).get("humOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(0).get("humOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(0).get("humOFFss"));
            HINIe = relayListhum.get(0).get("humINI")+getString(R.string.porcentaje);
            HFINe = relayListhum.get(0).get("humFIN")+getString(R.string.porcentaje);
            ontime.setText(ONe);
            oftime.setText(OFFe);
            huminicial.setText(HINIe);
            humfinal.setText(HFINe);
        }else if (relay==1&&!relayListhum.isEmpty()){
            ONe = Integer.parseInt(relayListhum.get(1).get("humONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(1).get("humONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(1).get("humONss"));
            OFFe = Integer.parseInt(relayListhum.get(1).get("humOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(1).get("humOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(1).get("humOFFss"));
            HINIe = relayListhum.get(1).get("humINI")+getString(R.string.porcentaje);
            HFINe = relayListhum.get(1).get("humFIN")+getString(R.string.porcentaje);
            ontime.setText(ONe);
            oftime.setText(OFFe);
            huminicial.setText(HINIe);
            humfinal.setText(HFINe);
        }else{
            if (relay==2&&!relayListhum.isEmpty()){
                ONe = Integer.parseInt(relayListhum.get(2).get("humONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(2).get("humONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(2).get("humONss"));
                OFFe = Integer.parseInt(relayListhum.get(2).get("humOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(2).get("humOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(2).get("humOFFss"));
                HINIe = relayListhum.get(2).get("humINI")+getString(R.string.porcentaje);
                HFINe = relayListhum.get(2).get("humFIN")+getString(R.string.porcentaje);
                ontime.setText(ONe);
                oftime.setText(OFFe);
                huminicial.setText(HINIe);
                humfinal.setText(HFINe);
            }else if (relay==3&&!relayListhum.isEmpty()){
                ONe = Integer.parseInt(relayListhum.get(3).get("humONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(3).get("humONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(3).get("humONss"));
                OFFe = Integer.parseInt(relayListhum.get(3).get("humOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(3).get("humOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListhum.get(3).get("humOFFss"));
                HINIe = relayListhum.get(3).get("humINI")+getString(R.string.porcentaje);
                HFINe = relayListhum.get(3).get("humFIN")+getString(R.string.porcentaje);
                ontime.setText(ONe);
                oftime.setText(OFFe);
                huminicial.setText(HINIe);
                humfinal.setText(HFINe);
            }
        }
    }

    private class solicitarDatos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            return conexion.getDatos(url[0]);
        }

        @Override
        protected void onPostExecute(String resultado) {
            if (resultado != null){
                Toast.makeText(getContext(), resultado,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), "error de conexion", Toast.LENGTH_LONG).show();
            }
        }
    }

}
