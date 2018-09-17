package com.example.cesar.temporizadorw.tabs;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.cesar.temporizadorw.comunication;

import com.example.cesar.temporizadorw.R;
import com.example.cesar.temporizadorw.conexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class humedad extends Fragment {

    TextView relayName,humedadvalue,huminicial,humfinal,ontime,oftime;
    FloatingActionButton saveBtn;
    Switch humedadswi;
    ImageButton huminicialbtn,humfinalbtn,ontimebtn,oftimebtn;
    NumberPicker hora, minutos, segundos;
    int relay;
    comunication mCallback;
    ProgressBar cargando;
    ArrayList<HashMap<String, String>> relayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        relayName = v.findViewById(R.id.relayName);
        saveBtn = v.findViewById(R.id.saveBtn);
        relay = 0;
        cargando = v.findViewById(R.id.progressBar);

        mCallback.sendata(1,relay);




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
            //selec();
            mCallback.sendata(0, relay);
            cargando.setVisibility(View.INVISIBLE);
        }

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
    }

}
