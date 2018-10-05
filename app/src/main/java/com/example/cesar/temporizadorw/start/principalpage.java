package com.example.cesar.temporizadorw.start;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cesar.temporizadorw.conexion;
import com.example.cesar.temporizadorw.show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class principalpage extends AppCompatActivity {

    SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ssid = getWifiName(getApplicationContext());
        System.out.println("el ssid es "+ssid);
        if(ssid.contains("TEMP")) {
            data=getSharedPreferences("datosDevice",Context.MODE_PRIVATE);
            new Gettimes().execute();
            Intent intent=new Intent(this,show.class);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(getApplicationContext(),"Contese al dispositivo",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            finish();
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

    public class Gettimes extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getData(){
            conexion sh = new conexion();
            String url = "http://192.168.4.1/info?id=stiotca&pass=1234567";
            String jsonStr = sh.makeServiceCall(url, "GET");

            SharedPreferences.Editor editor = data.edit();

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("data");
                    JSONArray time = jsonObj.getJSONArray("time");
                    JSONObject timee = time.getJSONObject(0);

                    //configuracion del reloj de tiempo real
                    String hora = timee.getString("hora");
                    String minutos = timee.getString("minutos");
                    String segundos = timee.getString("segundos");
                    String diaWeek = timee.getString("diaWeek");
                    String dia = timee.getString("dia");
                    String mes = timee.getString("mes");
                    String year = timee.getString("year");

                    editor.putString("hora", hora);
                    editor.putString("minutos", minutos);
                    editor.putString("segundos", segundos);
                    editor.putString("diaweek",diaWeek);
                    editor.putString("dia", dia);
                    editor.putString("mes", mes);
                    editor.putString("year", year);

                    editor.apply();
                    editor.commit();


                    // looping through All Times
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        //humedad
                        String humONhh = c.getString("hin_HH");
                        String humONmm = c.getString("hin_MM");
                        String humONss = c.getString("hin_SS");
                        String humOFFhh = c.getString("hfi_HH");
                        String humOFFmm = c.getString("hfi_MM");
                        String humOFFss = c.getString("hfi_SS");
                        String humINI = c.getString("HUMini");
                        String humFIN = c.getString("HUMfin");

                        //tiempo
                        String timeINhh = c.getString("timein_HH");
                        String timeONhh = c.getString("timeon_HH");
                        String timeOFFhh = c.getString("timeoff_HH");
                        String timeFIhh = c.getString("timefi_HH");
                        String timeINmm = c.getString("timein_MM");
                        String timeONmm = c.getString("timeon_MM");
                        String timeOFFmm = c.getString("timeoff_MM");
                        String timeFImm = c.getString("timefi_MM");
                        String timeINss = c.getString("timein_SS");
                        String timeONss = c.getString("timeon_SS");
                        String timeOFFss = c.getString("timeoff_SS");
                        String timeFIss = c.getString("timefi_SS");

                        //temperatura
                        String tempONhh = c.getString("tempin_HH");
                        String tempONmm = c.getString("tempin_MM");
                        String tempONss = c.getString("tempin_SS");
                        String tempOFFhh = c.getString("tempfi_HH");
                        String tempOFFmm = c.getString("tempfi_MM");
                        String tempOFFss = c.getString("tempfi_SS");
                        String tempINI = c.getString("TEMPini");
                        String tempFIN = c.getString("TEMPfin");

                        //humedad
                        editor.putString("humONhh"+i,humONhh);
                        editor.putString("humONmm"+i,humONmm);
                        editor.putString("humONss"+i,humONss);
                        editor.putString("humOFFhh"+i,humOFFhh);
                        editor.putString("humOFFmm"+i,humOFFmm);
                        editor.putString("humOFFss"+i,humOFFss);
                        editor.putString("humINI"+i,humINI);
                        editor.putString("humFIN"+i,humFIN);

                        //tiempo
                        editor.putString("timeINhh"+i,timeINhh);
                        editor.putString("timeINmm"+i,timeINmm);
                        editor.putString("timeINss"+i,timeINss);
                        editor.putString("timeONhh"+i,timeONhh);
                        editor.putString("timeONmm"+i,timeONmm);
                        editor.putString("timeONss"+i,timeONss);
                        editor.putString("timeOFFhh"+i,timeOFFhh);
                        editor.putString("timeOFFmm"+i,timeOFFmm);
                        editor.putString("timeOFFss"+i,timeOFFss);
                        editor.putString("timeFIhh"+i,timeFIhh);
                        editor.putString("timeFImm"+i,timeFImm);
                        editor.putString("timeFIss"+i,timeFIss);

                        //temperatura
                        editor.putString("tempONhh"+i,tempONhh);
                        editor.putString("tempONmm"+i,tempONmm);
                        editor.putString("tempONss"+i,tempONss);
                        editor.putString("tempOFFhh"+i,tempOFFhh);
                        editor.putString("tempOFFmm"+i,tempOFFmm);
                        editor.putString("tempOFFss"+i,tempOFFss);
                        editor.putString("tempINI"+i,tempINI);
                        editor.putString("tempFIN"+i,tempFIN);

                        //ajustes del reloj de tiempo real


                        editor.apply();
                        editor.commit();


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "error parsing datos: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "error parsing datos: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "No se reciben datos");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "no se reciben datos del servidor",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

}
