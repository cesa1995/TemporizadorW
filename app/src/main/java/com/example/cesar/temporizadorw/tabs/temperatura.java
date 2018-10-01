package com.example.cesar.temporizadorw.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.cesar.temporizadorw.comunication;
import com.example.cesar.temporizadorw.conexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class temperatura extends Fragment {

    TextView temperaturaValue,tempInicial,tempFinal,onTime,offTime;
    Switch temperaturaSwi;
    ImageButton tempInicialBtn,tempFinalBtn,onTimeBtn,offTimeBtn;
    NumberPicker hora, minutos, segundos, temperatura;
    int relay, hh, ss, mm, bon, temp;
    //comunication mCallback;
    ProgressBar cargando;
    ArrayList<HashMap<String, String>> relayListTemp;
    String ONe, OFFe, HINIe, HFINe, urlsave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        if (ssid.contains("TEMP")) {
            String RRR = String.valueOf(RR);
            urlsave ="http://192.168.4.1/config?ssid=stiotca&pass=1234567";
            //guardar solo datos de humedad
            if (RR > 8 && RR < 17 && tempp != null){
                switch (RR){
                    case 9:
                        relayListTemp.get(0).put("tempINI",tempp);
                        break;
                    case 10:
                        relayListTemp.get(0).put("tempFIN",tempp);
                        break;
                    case 11:
                        relayListTemp.get(1).put("tempINI",tempp);
                        break;
                    case 12:
                        relayListTemp.get(1).put("tempFIN",tempp);
                        break;
                    case 13:
                        relayListTemp.get(2).put("tempINI",tempp);
                        break;
                    case 14:
                        relayListTemp.get(2).put("tempFIN",tempp);
                        break;
                    case 15:
                        relayListTemp.get(3).put("tempINI",tempp);
                        break;
                    case 16:
                        relayListTemp.get(3).put("tempFIN",tempp);
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
                        break;
                    case 26:
                        relayListTemp.get(0).put("tempOFFhh", hhh);
                        relayListTemp.get(0).put("tempOFFmm", mmm);
                        relayListTemp.get(0).put("tempOFFss", sss);
                        break;
                    case 27:
                        relayListTemp.get(1).put("tempONhh", hhh);
                        relayListTemp.get(1).put("tempONmm", mmm);
                        relayListTemp.get(1).put("tempONss", sss);
                        break;
                    case 28:
                        relayListTemp.get(1).put("tempOFFhh", hhh);
                        relayListTemp.get(1).put("tempOFFmm", mmm);
                        relayListTemp.get(1).put("tempOFFss", sss);
                        break;
                    case 29:
                        relayListTemp.get(2).put("tempONhh", hhh);
                        relayListTemp.get(2).put("tempONmm", mmm);
                        relayListTemp.get(2).put("tempONss", sss);
                        break;
                    case 30:
                        relayListTemp.get(2).put("tempOFFhh", hhh);
                        relayListTemp.get(2).put("tempOFFmm", mmm);
                        relayListTemp.get(2).put("tempOFFss", sss);
                        break;
                    case 31:
                        relayListTemp.get(3).put("tempONhh", hhh);
                        relayListTemp.get(3).put("tempONmm", mmm);
                        relayListTemp.get(3).put("tempONss", sss);
                        break;
                    case 32:
                        relayListTemp.get(3).put("tempOFFhh", hhh);
                        relayListTemp.get(3).put("tempOFFmm", mmm);
                        relayListTemp.get(3).put("tempOFFss", sss);
                        break;
                }

                urlsave = urlsave + "&tconfig=2&hh=" + hhh + "&mm=" + mmm + "&ss=" + sss + "&R=" + RRR;
                System.out.println(urlsave);
            }


            new solicitarDatos().execute(urlsave);

            urlsave = "";

            selec();
        }else{
            Toast.makeText(getContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            getActivity().finish();
        }

    }



   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (comunication) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }*/

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
            conexion sh = new conexion();
            // Making a request to url and getting response
            String url = "http://192.168.4.1/info?id=stiotca&pass=1234567&tab=3";
            String jsonStr = sh.makeServiceCall(url, "GET");

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("info");
                    if (!relayListTemp.isEmpty()) {
                        //for (int i = 0; i < relayList.size(); i++) {
                        //  relayList.remove(i);
                        //}
                        relayListTemp.clear();
                    }
                    // looping through All Times
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String nombre = c.getString("nombre");
                        String tempONhh = c.getString("tempin_HH");
                        String tempONmm = c.getString("tempin_MM");
                        String tempONss = c.getString("tempin_SS");
                        String tempOFFhh = c.getString("tempfi_HH");
                        String tempOFFmm = c.getString("tempfi_MM");
                        String tempOFFss = c.getString("tempfi_SS");
                        String tempINI = c.getString("TEMini");
                        String tempFIN = c.getString("TEMfin");

                        // tmp hash map for single Times
                        HashMap<String, String> relay = new HashMap<>();

                        // adding each child node to HashMap key => value
                        relay.put("nombre", nombre);
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
                } catch (final JSONException e) {
                    Log.e(TAG, "error parsing datos: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "error parsing datos: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "No se reciben datos");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "no se reciben datos del servidor",
                                Toast.LENGTH_LONG).show();
                    }
                });
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