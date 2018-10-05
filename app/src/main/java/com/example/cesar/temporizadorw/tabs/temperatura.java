package com.example.cesar.temporizadorw.tabs;

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
import android.support.v4.app.Fragment;
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

public class temperatura extends Fragment {

    TextView temperaturaValue,tempInicial,tempFinal,onTime,offTime;
    Switch temperaturaSwi;
    ImageButton tempInicialBtn,tempFinalBtn,onTimeBtn,offTimeBtn;
    NumberPicker hora, minutos, segundos, temperatura;
    int relay, hh, ss, mm, bon, temp;
    ProgressBar cargando;
    ArrayList<HashMap<String, String>> relayListTemp;
    String ONe, OFFe, HINIe, HFINe, urlsave;

    SharedPreferences data;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temperatura, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        temperaturaSwi = v.findViewById(R.id.temperaturaswi);
        temperaturaValue = v.findViewById(R.id.temperaturaActual);
        tempInicial = v.findViewById(R.id.tempinicial);
        tempFinal = v.findViewById(R.id.tempfinal);
        onTime = v.findViewById(R.id.tempontime);
        offTime = v.findViewById(R.id.tempoftime);
        tempInicialBtn = v.findViewById(R.id.tempinicialbtn);
        tempFinalBtn = v.findViewById(R.id.tempfinalbtn);
        onTimeBtn = v.findViewById(R.id.ontimebtn);
        offTimeBtn = v.findViewById(R.id.oftimebtn);
        relayListTemp = new ArrayList<>();
        relay = 0;
        cargando = v.findViewById(R.id.progressBar);
        data = this.getActivity().getSharedPreferences("datosDevice",Context.MODE_PRIVATE);

        new Gettimes().execute();

        tempInicialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon = boton(1,relay);
                dialogtemp(bon,temp);
                selec();
            }
        });

        tempFinalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon = boton(2,relay);
                dialogtemp(bon,temp);
                selec();
            }
        });

        onTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon = boton(3, relay);
                dialog(bon, hh, mm, ss);
                selec();
            }
        });

        offTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bon = boton(4, relay);
                dialog(bon, hh, mm, ss);
                selec();
            }
        });

    }

    public void dialogtemp(final int RR, int hum){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View d = inflater.inflate(R.layout.picker_hum_temp,null);
        builder.setView(d);
        builder.setTitle("Selecciones temperatura:");
        builder.setMessage("°C");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hum = String.valueOf(temperatura.getValue());
                guardar(RR,null,null,null, hum);
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        temperatura = d.findViewById(R.id.humtemp);

        temperatura.setMaxValue(100);
        temperatura.setMinValue(0);

        temperatura.setValue(hum);

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
        assert manager != null;
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
                        temp=Integer.parseInt(relayListTemp.get(0).get("tempINI"));
                        RR = 9;
                        break;
                    case 2:
                        temp=Integer.parseInt(relayListTemp.get(0).get("tempFIN"));
                        RR = 10;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListTemp.get(0).get("tempONhh"));
                        mm=Integer.parseInt(relayListTemp.get(0).get("tempONmm"));
                        ss=Integer.parseInt(relayListTemp.get(0).get("tempONss"));
                        RR = 25;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListTemp.get(0).get("tempOFFhh"));
                        mm=Integer.parseInt(relayListTemp.get(0).get("tempOFFmm"));
                        ss=Integer.parseInt(relayListTemp.get(0).get("tempOFFss"));
                        RR = 26;
                        break;
                }
                break;
            case 1:
                switch (boton) {
                    case 1:
                        temp=Integer.parseInt(relayListTemp.get(1).get("tempINI"));
                        RR = 11;
                        break;
                    case 2:
                        temp=Integer.parseInt(relayListTemp.get(1).get("tempFIN"));
                        RR = 12;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListTemp.get(1).get("tempONhh"));
                        mm=Integer.parseInt(relayListTemp.get(1).get("tempONmm"));
                        ss=Integer.parseInt(relayListTemp.get(1).get("tempONss"));
                        RR = 27;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListTemp.get(1).get("tempOFFhh"));
                        mm=Integer.parseInt(relayListTemp.get(1).get("tempOFFmm"));
                        ss=Integer.parseInt(relayListTemp.get(1).get("tempOFFss"));
                        RR = 28;
                        break;
                }
                break;
            case 2:
                switch (boton) {
                    case 1:
                        temp=Integer.parseInt(relayListTemp.get(2).get("tempINI"));
                        RR = 13;
                        break;
                    case 2:
                        temp=Integer.parseInt(relayListTemp.get(2).get("tempFIN"));
                        RR = 14;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListTemp.get(2).get("tempONhh"));
                        mm=Integer.parseInt(relayListTemp.get(2).get("tempONmm"));
                        ss=Integer.parseInt(relayListTemp.get(2).get("tempONss"));
                        RR = 29;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListTemp.get(2).get("tempOFFhh"));
                        mm=Integer.parseInt(relayListTemp.get(2).get("tempOFFmm"));
                        ss=Integer.parseInt(relayListTemp.get(2).get("tempOFFss"));
                        RR = 30;
                        break;
                }
                break;
            case 3:
                switch (boton) {
                    case 1:
                        temp=Integer.parseInt(relayListTemp.get(3).get("tempINI"));
                        RR = 15;
                        break;
                    case 2:
                        temp=Integer.parseInt(relayListTemp.get(3).get("tempFIN"));
                        RR = 16;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayListTemp.get(3).get("tempONhh"));
                        mm=Integer.parseInt(relayListTemp.get(3).get("tempONmm"));
                        ss=Integer.parseInt(relayListTemp.get(3).get("tempONss"));
                        RR = 31;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayListTemp.get(3).get("tempOFFhh"));
                        mm=Integer.parseInt(relayListTemp.get(3).get("tempOFFmm"));
                        ss=Integer.parseInt(relayListTemp.get(3).get("tempOFFss"));
                        RR = 32;
                        break;
                }
                break;
        }
        return RR;
    }

    public void guardar(int RR, String hhh, String mmm, String sss, String tempp){
        String ssid = getWifiName(getContext());
        SharedPreferences.Editor editor = data.edit();
        if (ssid.contains("TEMP")) {
            String RRR = String.valueOf(RR);
            urlsave ="http://192.168.4.1/config?ssid=stiotca&pass=1234567";
            //guardar solo datos de humedad
            if (RR > 8 && RR < 17 && tempp != null){
                switch (RR){
                    case 9:
                        relayListTemp.get(0).put("tempINI",tempp);
                        editor.putString("tempINI0",tempp);
                        break;
                    case 10:
                        relayListTemp.get(0).put("tempFIN",tempp);
                        editor.putString("tempFIN0",tempp);
                        break;
                    case 11:
                        relayListTemp.get(1).put("tempINI",tempp);
                        editor.putString("tempINI1",tempp);
                        break;
                    case 12:
                        relayListTemp.get(1).put("tempFIN",tempp);
                        editor.putString("tempFIN1",tempp);
                        break;
                    case 13:
                        relayListTemp.get(2).put("tempINI",tempp);
                        editor.putString("tempINI2",tempp);
                        break;
                    case 14:
                        relayListTemp.get(2).put("tempFIN",tempp);
                        editor.putString("tempFIN2",tempp);
                        break;
                    case 15:
                        relayListTemp.get(3).put("tempINI",tempp);
                        editor.putString("tempINI3",tempp);
                        break;
                    case 16:
                        relayListTemp.get(3).put("tempFIN",tempp);
                        editor.putString("tempFIN3",tempp);
                        break;
                }

                urlsave = urlsave+"&tconfig=0&humTemp=" +tempp+ "&R=" + RRR;
                System.out.println(urlsave);

            }else {
                switch (RR) {
                    case 25:
                        relayListTemp.get(0).put("tempONhh", hhh);
                        relayListTemp.get(0).put("tempONmm", mmm);
                        relayListTemp.get(0).put("tempONss", sss);
                        editor.putString("tempONhh0",String.valueOf(hhh));
                        editor.putString("tempONmm0",String.valueOf(mmm));
                        editor.putString("tempONss0",String.valueOf(sss));
                        break;
                    case 26:
                        relayListTemp.get(0).put("tempOFFhh", hhh);
                        relayListTemp.get(0).put("tempOFFmm", mmm);
                        relayListTemp.get(0).put("tempOFFss", sss);
                        editor.putString("tempOFFhh0",String.valueOf(hhh));
                        editor.putString("tempOFFmm0",String.valueOf(mmm));
                        editor.putString("tempOFFss0",String.valueOf(sss));
                        break;
                    case 27:
                        relayListTemp.get(1).put("tempONhh", hhh);
                        relayListTemp.get(1).put("tempONmm", mmm);
                        relayListTemp.get(1).put("tempONss", sss);
                        editor.putString("tempONhh1",String.valueOf(hhh));
                        editor.putString("tempONmm1",String.valueOf(mmm));
                        editor.putString("tempONss1",String.valueOf(sss));
                        break;
                    case 28:
                        relayListTemp.get(1).put("tempOFFhh", hhh);
                        relayListTemp.get(1).put("tempOFFmm", mmm);
                        relayListTemp.get(1).put("tempOFFss", sss);
                        editor.putString("tempOFFhh1",String.valueOf(hhh));
                        editor.putString("tempOFFmm1",String.valueOf(mmm));
                        editor.putString("tempOFFss1",String.valueOf(sss));
                        break;
                    case 29:
                        relayListTemp.get(2).put("tempONhh", hhh);
                        relayListTemp.get(2).put("tempONmm", mmm);
                        relayListTemp.get(2).put("tempONss", sss);
                        editor.putString("tempONhh2",String.valueOf(hhh));
                        editor.putString("tempONmm2",String.valueOf(mmm));
                        editor.putString("tempONss2",String.valueOf(sss));
                        break;
                    case 30:
                        relayListTemp.get(2).put("tempOFFhh", hhh);
                        relayListTemp.get(2).put("tempOFFmm", mmm);
                        relayListTemp.get(2).put("tempOFFss", sss);
                        editor.putString("tempOFFhh2",String.valueOf(hhh));
                        editor.putString("tempOFFmm2",String.valueOf(mmm));
                        editor.putString("tempOFFss2",String.valueOf(sss));
                        break;
                    case 31:
                        relayListTemp.get(3).put("tempONhh", hhh);
                        relayListTemp.get(3).put("tempONmm", mmm);
                        relayListTemp.get(3).put("tempONss", sss);
                        editor.putString("tempONhh3",String.valueOf(hhh));
                        editor.putString("tempONmm3",String.valueOf(mmm));
                        editor.putString("tempONss3",String.valueOf(sss));
                        break;
                    case 32:
                        relayListTemp.get(3).put("tempOFFhh", hhh);
                        relayListTemp.get(3).put("tempOFFmm", mmm);
                        relayListTemp.get(3).put("tempOFFss", sss);
                        editor.putString("tempOFFhh3",String.valueOf(hhh));
                        editor.putString("tempOFFmm3",String.valueOf(mmm));
                        editor.putString("tempOFFss3",String.valueOf(sss));
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
                    if (!relayListTemp.isEmpty()) {
                        //for (int i = 0; i < relayList.size(); i++) {
                        //  relayList.remove(i);
                        //}
                        relayListTemp.clear();
                    }
                    // looping through All Times
                    for (int i = 0; i < 4; i++) {
                        String tempONhh = data.getString("tempONhh"+i, "0");
                        String tempONmm = data.getString("tempONmm"+i, "0");
                        String tempONss = data.getString("tempONss"+i, "0");
                        String tempOFFhh = data.getString("tempOFFhh"+i, "0");
                        String tempOFFmm = data.getString("tempOFFmm"+i, "0");
                        String tempOFFss = data.getString("tempOFFss"+i, "0");
                        String tempINI = data.getString("tempINI"+i, "0");
                        String tempFIN = data.getString("tempFIN"+i, "0");

                        // tmp hash map for single Times
                        HashMap<String, String> relay = new HashMap<>();

                        // adding each child node to HashMap key => value
                        relay.put("tempONhh", tempONhh);
                        relay.put("tempONmm", tempONmm);
                        relay.put("tempONss", tempONss);
                        relay.put("tempOFFhh", tempOFFhh);
                        relay.put("tempOFFmm", tempOFFmm);
                        relay.put("tempOFFss", tempOFFss);
                        relay.put("tempINI", tempINI);
                        relay.put("tempFIN", tempFIN);

                        // adding contact to contact list
                        relayListTemp.add(relay);

                        System.out.println(relayListTemp.toString());

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

        if (relay==0 &&!relayListTemp.isEmpty()){
            ONe = Integer.parseInt(relayListTemp.get(0).get("tempONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(0).get("tempONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(0).get("tempONss"));
            OFFe = Integer.parseInt(relayListTemp.get(0).get("tempOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(0).get("tempOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(0).get("tempOFFss"));
            HINIe = relayListTemp.get(0).get("tempINI")+getString(R.string.centigrados);
            HFINe = relayListTemp.get(0).get("tempFIN")+getString(R.string.centigrados);
            onTime.setText(ONe);
            offTime.setText(OFFe);
            tempInicial.setText(HINIe);
            tempFinal.setText(HFINe);
        }else if (relay==1&&!relayListTemp.isEmpty()){
            ONe = Integer.parseInt(relayListTemp.get(1).get("tempONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(1).get("tempONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(1).get("tempONss"));
            OFFe = Integer.parseInt(relayListTemp.get(1).get("tempOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(1).get("tempOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(1).get("tempOFFss"));
            HINIe = relayListTemp.get(1).get("tempINI")+getString(R.string.centigrados);
            HFINe = relayListTemp.get(1).get("tempFIN")+getString(R.string.centigrados);
            onTime.setText(ONe);
            offTime.setText(OFFe);
            tempInicial.setText(HINIe);
            tempFinal.setText(HFINe);
        }else{
            if (relay==2&&!relayListTemp.isEmpty()){
                ONe = Integer.parseInt(relayListTemp.get(2).get("tempONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(2).get("tempONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(2).get("tempONss"));
                OFFe = Integer.parseInt(relayListTemp.get(2).get("tempOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(2).get("tempOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(2).get("tempOFFss"));
                HINIe = relayListTemp.get(2).get("tempINI")+getString(R.string.centigrados);
                HFINe = relayListTemp.get(2).get("tempFIN")+getString(R.string.centigrados);
                onTime.setText(ONe);
                offTime.setText(OFFe);
                tempInicial.setText(HINIe);
                tempFinal.setText(HFINe);
            }else if (relay==3&&!relayListTemp.isEmpty()){
                ONe = Integer.parseInt(relayListTemp.get(3).get("tempONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(3).get("tempONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(3).get("tempONss"));
                OFFe = Integer.parseInt(relayListTemp.get(3).get("tempOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(3).get("tempOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayListTemp.get(3).get("tempOFFss"));
                HINIe = relayListTemp.get(3).get("tempINI")+getString(R.string.centigrados);
                HFINe = relayListTemp.get(3).get("tempFIN")+getString(R.string.centigrados);
                onTime.setText(ONe);
                offTime.setText(OFFe);
                tempInicial.setText(HINIe);
                tempFinal.setText(HFINe);
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