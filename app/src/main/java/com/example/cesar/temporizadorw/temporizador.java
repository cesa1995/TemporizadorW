package com.example.cesar.temporizadorw;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class temporizador extends Fragment{

    FloatingActionButton saveBtn;
    TextView relayName, on, off, horaini, horafin;
    ImageButton onbtn, offbtn, horainibtn, horafinbtn;
    ToggleButton I,II,III,IV;
    String urlsave;
    String ONe, OFFe, INIe, FINe,tittle;
    int relay, Re;
    ArrayList<HashMap<String, String>> relayList;
    NumberPicker hora,minutos,segundos;
    int hh,mm,ss,bon;
    //ArrayList<Map.Entry<String,String>> entryArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temporizador, container, false);
       initUI(v);
       return v;
    }


    private void initUI(View v) {
        relayList = new ArrayList<>();
        I = v.findViewById(R.id.I);
        II = v.findViewById(R.id.II);
        III = v.findViewById(R.id.III);
        IV = v.findViewById(R.id.IV);
        saveBtn = v.findViewById(R.id.saveBtn);
        on = v.findViewById(R.id.on);
        off = v.findViewById(R.id.off);
        horaini = v.findViewById(R.id.horaini);
        horafin = v.findViewById(R.id.horafin);
        onbtn = v.findViewById(R.id.onbtn);
        offbtn = v.findViewById(R.id.offbtn);
        horainibtn = v.findViewById(R.id.horainibtn);
        horafinbtn = v.findViewById(R.id.horafinbtn);

        I.setChecked(true);
        II.setChecked(true);
        III.setChecked(true);
        IV.setChecked(true);

        new Gettimes().execute();

        relayName = v.findViewById(R.id.relayName);
            I.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!relayList.isEmpty()) {
                        if (!isChecked) {
                            I.setChecked(false);
                        } else {
                            selec(I);
                        }
                    }
                }
            });

            II.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!relayList.isEmpty()) {
                        if (!isChecked) {
                            II.setChecked(false);
                        } else {
                            selec(II);
                        }
                    }
                }
            });

            III.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!relayList.isEmpty()) {
                        if (!isChecked) {
                            III.setChecked(false);
                        } else {
                            selec(III);
                        }
                    }
                }
            });

            IV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!relayList.isEmpty()) {
                        if (!isChecked) {
                            IV.setChecked(false);
                        } else {
                            selec(IV);
                        }
                    }
                }
            });

            onbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon = boton(1,Re);
                    dialog(bon,hh,mm,ss);
                }
            });

            offbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon = boton(2,Re);
                    dialog(bon,hh,mm,ss);
                }
            });

            horainibtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon = boton(3,Re);
                    dialog(bon,hh,mm,ss);
                }
            });

            horafinbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon = boton(4,Re);
                    dialog(bon,hh,mm,ss);
                }
            });

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
                enviar(hh,mm,ss,RR);
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
        segundos.setMinValue(0);
        segundos.setMaxValue(59);
        hora.setValue(hh);
        minutos.setValue(mm);
        segundos.setValue(ss);

        builder.create();
        builder.show();
    }

    public void enviar(String hh,String mm,String ss,int RR){
        String RRR=String.valueOf(RR);
        String ssid = getWifiName(getContext());
        if (ssid.contains("TEMP")) {
            urlsave = "http://192.168.4.1/config?tconfig=2&ssid=stiotca&pass=1234567&hh=" + hh + "&mm=" + mm + "&ss=" + ss + "&R=" + RRR;
            System.out.println(urlsave);

            new solicitarDatos().execute(urlsave);

            urlsave = "";
        }else{
            Toast.makeText(getContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            getActivity().finish();
        }

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
            case 1:
                switch (boton) {
                    case 1:
                        hh=Integer.parseInt(relayList.get(0).get("timeONhh"));
                        mm=Integer.parseInt(relayList.get(0).get("timeONmm"));
                        ss=Integer.parseInt(relayList.get(0).get("timeONss"));
                        RR = 1;
                        break;
                    case 2:
                        hh=Integer.parseInt(relayList.get(0).get("timeOFFhh"));
                        mm=Integer.parseInt(relayList.get(0).get("timeOFFmm"));
                        ss=Integer.parseInt(relayList.get(0).get("timeOFFss"));
                        RR = 2;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayList.get(0).get("timeINhh"));
                        mm=Integer.parseInt(relayList.get(0).get("timeINmm"));
                        ss=Integer.parseInt(relayList.get(0).get("timeINss"));
                        RR = 3;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayList.get(0).get("timeFIhh"));
                        mm=Integer.parseInt(relayList.get(0).get("timeFImm"));
                        ss=Integer.parseInt(relayList.get(0).get("timeFIss"));
                        RR = 4;
                        break;
                }
                break;
            case 2:
                switch (boton) {
                    case 1:
                        hh=Integer.parseInt(relayList.get(1).get("timeONhh"));
                        mm=Integer.parseInt(relayList.get(1).get("timeONmm"));
                        ss=Integer.parseInt(relayList.get(1).get("timeONss"));
                        RR = 5;
                        break;
                    case 2:
                        hh=Integer.parseInt(relayList.get(1).get("timeOFFhh"));
                        mm=Integer.parseInt(relayList.get(1).get("timeOFFmm"));
                        ss=Integer.parseInt(relayList.get(1).get("timeOFFss"));
                        RR = 6;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayList.get(1).get("timeINhh"));
                        mm=Integer.parseInt(relayList.get(1).get("timeINmm"));
                        ss=Integer.parseInt(relayList.get(1).get("timeINss"));
                        RR = 7;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayList.get(1).get("timeFIhh"));
                        mm=Integer.parseInt(relayList.get(1).get("timeFImm"));
                        ss=Integer.parseInt(relayList.get(1).get("timeFIss"));
                        RR = 8;
                        break;
                }
                break;
            case 3:
                switch (boton) {
                    case 1:
                        hh=Integer.parseInt(relayList.get(2).get("timeONhh"));
                        mm=Integer.parseInt(relayList.get(2).get("timeONmm"));
                        ss=Integer.parseInt(relayList.get(2).get("timeONss"));
                        RR = 9;
                        break;
                    case 2:
                        hh=Integer.parseInt(relayList.get(2).get("timeOFFhh"));
                        mm=Integer.parseInt(relayList.get(2).get("timeOFFmm"));
                        ss=Integer.parseInt(relayList.get(2).get("timeOFFss"));
                        RR = 10;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayList.get(2).get("timeINhh"));
                        mm=Integer.parseInt(relayList.get(2).get("timeINmm"));
                        ss=Integer.parseInt(relayList.get(2).get("timeINss"));
                        RR = 11;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayList.get(2).get("timeFIhh"));
                        mm=Integer.parseInt(relayList.get(2).get("timeFImm"));
                        ss=Integer.parseInt(relayList.get(2).get("timeFIss"));
                        RR = 12;
                        break;
                }
                break;
            case 4:
                switch (boton) {
                    case 1:
                        hh=Integer.parseInt(relayList.get(3).get("timeONhh"));
                        mm=Integer.parseInt(relayList.get(3).get("timeONmm"));
                        ss=Integer.parseInt(relayList.get(3).get("timeONss"));
                        RR = 13;
                        break;
                    case 2:
                        hh=Integer.parseInt(relayList.get(3).get("timeOFFhh"));
                        mm=Integer.parseInt(relayList.get(3).get("timeOFFmm"));
                        ss=Integer.parseInt(relayList.get(3).get("timeOFFss"));
                        RR = 14;
                        break;
                    case 3:
                        hh=Integer.parseInt(relayList.get(3).get("timeINhh"));
                        mm=Integer.parseInt(relayList.get(3).get("timeINmm"));
                        ss=Integer.parseInt(relayList.get(3).get("timeINss"));
                        RR = 15;
                        break;
                    case 4:
                        hh=Integer.parseInt(relayList.get(3).get("timeFIhh"));
                        mm=Integer.parseInt(relayList.get(3).get("timeFImm"));
                        ss=Integer.parseInt(relayList.get(3).get("timeFIss"));
                        RR = 16;
                        break;
                }
                break;
        }
        return RR;
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
                new Gettimes().execute();
            }else {
                Toast.makeText(getContext(), "error de conexion", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void selec(ToggleButton relay){
        if (relay.equals(I)&&!relayList.isEmpty()){
            II.setChecked(false);
            III.setChecked(false);
            IV.setChecked(false);
            Re =Integer.parseInt(relayList.get(0).get("nombre"));
            ONe = Integer.parseInt(relayList.get(0).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeONss"));
            OFFe = Integer.parseInt(relayList.get(0).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeOFFss"));
            INIe = Integer.parseInt(relayList.get(0).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeINss"));
            FINe = Integer.parseInt(relayList.get(0).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeFIss"));
            on.setText(ONe);
            off.setText(OFFe);
            horaini.setText(INIe);
            horafin.setText(FINe);
        }else if (relay.equals(II)&&!relayList.isEmpty()){
                I.setChecked(false);
                III.setChecked(false);
                IV.setChecked(false);
                Re =Integer.parseInt(relayList.get(1).get("nombre"));
            ONe = Integer.parseInt(relayList.get(1).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeONss"));
            OFFe = Integer.parseInt(relayList.get(1).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeOFFss"));
            INIe = Integer.parseInt(relayList.get(1).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeINss"));
            FINe = Integer.parseInt(relayList.get(1).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeFIss"));
            on.setText(ONe);
            off.setText(OFFe);
            horaini.setText(INIe);
            horafin.setText(FINe);
        }else{
            if (relay.equals(III)&&!relayList.isEmpty()){
                I.setChecked(false);
                II.setChecked(false);
                IV.setChecked(false);
                Re =Integer.parseInt(relayList.get(2).get("nombre"));
                ONe = Integer.parseInt(relayList.get(2).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeONss"));
                OFFe = Integer.parseInt(relayList.get(2).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeOFFss"));
                INIe = Integer.parseInt(relayList.get(2).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeINss"));
                FINe = Integer.parseInt(relayList.get(2).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeFIss"));
                on.setText(ONe);
                off.setText(OFFe);
                horaini.setText(INIe);
                horafin.setText(FINe);
            }else if (relay.equals(IV)&&!relayList.isEmpty()){
                II.setChecked(false);
                III.setChecked(false);
                I.setChecked(false);
                Re =Integer.parseInt(relayList.get(3).get("nombre"));
                ONe = Integer.parseInt(relayList.get(3).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeONss"));
                OFFe = Integer.parseInt(relayList.get(3).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeOFFss"));
                INIe = Integer.parseInt(relayList.get(3).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeINss"));
                FINe = Integer.parseInt(relayList.get(3).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(3).get("timeFIss"));
                on.setText(ONe);
                off.setText(OFFe);
                horaini.setText(INIe);
                horafin.setText(FINe);
            }
        }
        tittle = getString(R.string.Tittle)+" "+Re;
        relayName.setText(tittle);
    }

    public class Gettimes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            conexion sh = new conexion();
            // Making a request to url and getting response
            String url = "http://192.168.4.1/info?id=stiotca&pass=1234567&tab=2";
            String jsonStr = sh.makeServiceCall(url, "GET");

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("info");
                    if (!relayList.isEmpty()) {
                        //for (int i = 0; i < relayList.size(); i++) {
                          //  relayList.remove(i);
                        //}
                        relayList.clear();
                    }
                    // looping through All Times
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String nombre = c.getString("nombre");
                        String timeINhh = c.getString("tin_HH");
                        String timeONhh = c.getString("ton_HH");
                        String timeOFFhh = c.getString("tof_HH");
                        String timeFIhh = c.getString("tfi_HH");
                        String timeINmm = c.getString("tin_MM");
                        String timeONmm = c.getString("ton_MM");
                        String timeOFFmm = c.getString("tof_MM");
                        String timeFImm = c.getString("tfi_MM");
                        String timeINss = c.getString("tin_SS");
                        String timeONss = c.getString("ton_SS");
                        String timeOFFss = c.getString("tof_SS");
                        String timeFIss = c.getString("tfi_SS");

                        // tmp hash map for single Times
                        HashMap<String, String> relay = new HashMap<>();

                        // adding each child node to HashMap key => value
                        relay.put("nombre", nombre);
                        relay.put("timeINhh", timeINhh);
                        relay.put("timeINmm", timeINmm);
                        relay.put("timeINss", timeINss);
                        relay.put("timeONhh", timeONhh);
                        relay.put("timeONmm", timeONmm);
                        relay.put("timeONss", timeONss);
                        relay.put("timeOFFhh", timeOFFhh);
                        relay.put("timeOFFmm", timeOFFmm);
                        relay.put("timeOFFss", timeOFFss);
                        relay.put("timeFIhh", timeFIhh);
                        relay.put("timeFImm", timeFImm);
                        relay.put("timeFIss", timeFIss);

                        // adding contact to contact list
                        relayList.add(relay);

                        System.out.println(relayList.toString());

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
            super.onPostExecute(result);

            if (I.isChecked()){
                selec(I);
            }else if (II.isChecked()){
                selec(II);
            }else{
                if (III.isChecked()){
                    selec(III);
                }else if(IV.isChecked()){
                    selec(IV);
                }
            }


        }

    }
}