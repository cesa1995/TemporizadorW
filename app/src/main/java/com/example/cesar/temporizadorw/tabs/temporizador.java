package com.example.cesar.temporizadorw.tabs;

import android.annotation.SuppressLint;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cesar.temporizadorw.R;
import com.example.cesar.temporizadorw.comunication;
import com.example.cesar.temporizadorw.conexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class temporizador extends Fragment{

    FloatingActionButton saveBtn;
    TextView on, off, horaini, horafin;
    ImageButton onbtn, offbtn, horainibtn, horafinbtn;
    String ONe, OFFe, INIe, FINe, urlsave;
    int relay;
    NumberPicker hora,minutos,segundos;
    int hh,mm,ss,bon;
    ArrayList<HashMap<String, String>> relayList;
    comunication mCallback;
    ProgressBar cargando;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_temporizador, container, false);
       initUI(v);
       return v;
    }


    private void initUI(View v) {
        on = v.findViewById(R.id.on);
        off = v.findViewById(R.id.off);
        horaini = v.findViewById(R.id.horaini);
        horafin = v.findViewById(R.id.horafin);
        onbtn = v.findViewById(R.id.onbtn);
        offbtn = v.findViewById(R.id.offbtn);
        horainibtn = v.findViewById(R.id.horainibtn);
        horafinbtn = v.findViewById(R.id.horafinbtn);
        relayList = new ArrayList<>();
        cargando = v.findViewById(R.id.progressBar);
        relay=0;

        new Gettimes().execute();

            onbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon=boton(1,relay);
                    dialog(bon,hh,mm,ss);
                    selec();
                }
            });

            offbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon=boton(2,relay);
                    dialog(bon,hh,mm,ss);
                    selec();
                }
            });

            horainibtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon=boton(3,relay);
                    dialog(bon,hh,mm,ss);
                    selec();
                }
            });

            horafinbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bon=boton(4,relay);
                    dialog(bon,hh,mm,ss);
                    selec();
                }
            });

    }

    @Override
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
            selec();
            mCallback.sendata(0, relay);
            cargando.setVisibility(View.INVISIBLE);
        }

    }

    public void guardar(int RR, int hhh,int mmm,int sss){
        String ssid = getWifiName(getContext());
        if (ssid.contains("TEMP")) {
            String RRR=String.valueOf(RR);
            String hh=String.valueOf(hhh);
            String mm=String.valueOf(mmm);
            String ss=String.valueOf(sss);
            switch (RR){
                case 1:
                    relayList.get(0).put("timeONhh",String.valueOf(hhh));
                    relayList.get(0).put("timeONmm",String.valueOf(mmm));
                    relayList.get(0).put("timeONss",String.valueOf(sss));
                    break;
                case 2:
                    relayList.get(0).put("timeOFFhh",String.valueOf(hhh));
                    relayList.get(0).put("timeOFFmm",String.valueOf(mmm));
                    relayList.get(0).put("timeOFFss",String.valueOf(sss));
                    break;
                case 3:
                    relayList.get(0).put("timeINhh",String.valueOf(hhh));
                    relayList.get(0).put("timeINmm",String.valueOf(mmm));
                    relayList.get(0).put("timeINss",String.valueOf(sss));
                    break;
                case 4:
                    relayList.get(0).put("timeFIhh",String.valueOf(hhh));
                    relayList.get(0).put("timeFImm",String.valueOf(mmm));
                    relayList.get(0).put("timeFIss",String.valueOf(sss));
                    break;
                case 5:
                    relayList.get(1).put("timeONhh",String.valueOf(hhh));
                    relayList.get(1).put("timeONmm",String.valueOf(mmm));
                    relayList.get(1).put("timeONss",String.valueOf(sss));
                    break;
                case 6:
                    relayList.get(1).put("timeOFFhh",String.valueOf(hhh));
                    relayList.get(1).put("timeOFFmm",String.valueOf(mmm));
                    relayList.get(1).put("timeOFFss",String.valueOf(sss));
                    break;
                case 7:
                    relayList.get(1).put("timeINhh",String.valueOf(hhh));
                    relayList.get(1).put("timeINmm",String.valueOf(mmm));
                    relayList.get(1).put("timeINss",String.valueOf(sss));
                    break;
                case 8:
                    relayList.get(1).put("timeFIhh",String.valueOf(hhh));
                    relayList.get(1).put("timeFImm",String.valueOf(mmm));
                    relayList.get(1).put("timeFIss",String.valueOf(sss));
                    break;
                case 9:
                    relayList.get(2).put("timeONhh",String.valueOf(hhh));
                    relayList.get(2).put("timeONmm",String.valueOf(mmm));
                    relayList.get(2).put("timeONss",String.valueOf(sss));
                    break;
                case 10:
                    relayList.get(2).put("timeOFFhh",String.valueOf(hhh));
                    relayList.get(2).put("timeOFFmm",String.valueOf(mmm));
                    relayList.get(2).put("timeOFFss",String.valueOf(sss));
                    break;
                case 11:
                    relayList.get(2).put("timeINhh",String.valueOf(hhh));
                    relayList.get(2).put("timeINmm",String.valueOf(mmm));
                    relayList.get(2).put("timeINss",String.valueOf(sss));
                    break;
                case 12:
                    relayList.get(2).put("timeFIhh",String.valueOf(hhh));
                    relayList.get(2).put("timeFImm",String.valueOf(mmm));
                    relayList.get(2).put("timeFIss",String.valueOf(sss));
                    break;
                case 13:
                    relayList.get(3).put("timeONhh",String.valueOf(hhh));
                    relayList.get(3).put("timeONmm",String.valueOf(mmm));
                    relayList.get(3).put("timeONss",String.valueOf(sss));
                    break;
                case 14:
                    relayList.get(3).put("timeOFFhh",String.valueOf(hhh));
                    relayList.get(3).put("timeOFFmm",String.valueOf(mmm));
                    relayList.get(3).put("timeOFFss",String.valueOf(sss));
                    break;
                case 15:
                    relayList.get(3).put("timeINhh",String.valueOf(hhh));
                    relayList.get(3).put("timeINmm",String.valueOf(mmm));
                    relayList.get(3).put("timeINss",String.valueOf(sss));
                    break;
                case 16:
                    relayList.get(3).put("timeFIhh",String.valueOf(hhh));
                    relayList.get(3).put("timeFImm",String.valueOf(mmm));
                    relayList.get(3).put("timeFIss",String.valueOf(sss));
                    break;
            }

            urlsave = "http://192.168.4.1/config?tconfig=2&ssid=stiotca&pass=1234567&hh=" + hh + "&mm=" + mm + "&ss=" + ss + "&R=" + RRR;
            System.out.println(urlsave);

            new solicitarDatos().execute(urlsave);

            urlsave = "";

            selec();
        }else{
            Toast.makeText(getContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            getActivity().finish();
        }

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
                int hh = hora.getValue();
                int mm = minutos.getValue();
                int ss = segundos.getValue();
                guardar(RR,hh,mm,ss);
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
            case 1:
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
            case 2:
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
            case 3:
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

    public void selec(){

        if (relay==0 &&!relayList.isEmpty()){
            ONe = Integer.parseInt(relayList.get(0).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeONss"));
            OFFe = Integer.parseInt(relayList.get(0).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeOFFss"));
            INIe = Integer.parseInt(relayList.get(0).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeINss"));
            FINe = Integer.parseInt(relayList.get(0).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(0).get("timeFIss"));
            on.setText(ONe);
            off.setText(OFFe);
            horaini.setText(INIe);
            horafin.setText(FINe);
        }else if (relay==1&&!relayList.isEmpty()){
            ONe = Integer.parseInt(relayList.get(1).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeONss"));
            OFFe = Integer.parseInt(relayList.get(1).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeOFFss"));
            INIe = Integer.parseInt(relayList.get(1).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeINss"));
            FINe = Integer.parseInt(relayList.get(1).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(1).get("timeFIss"));
            on.setText(ONe);
            off.setText(OFFe);
            horaini.setText(INIe);
            horafin.setText(FINe);
        }else{
            if (relay==2&&!relayList.isEmpty()){
                ONe = Integer.parseInt(relayList.get(2).get("timeONhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeONmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeONss"));
                OFFe = Integer.parseInt(relayList.get(2).get("timeOFFhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeOFFmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeOFFss"));
                INIe = Integer.parseInt(relayList.get(2).get("timeINhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeINmm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeINss"));
                FINe = Integer.parseInt(relayList.get(2).get("timeFIhh"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeFImm"))+" "+getString(R.string.puntos)+" "+Integer.parseInt(relayList.get(2).get("timeFIss"));
                on.setText(ONe);
                off.setText(OFFe);
                horaini.setText(INIe);
                horafin.setText(FINe);
            }else if (relay==3&&!relayList.isEmpty()){
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