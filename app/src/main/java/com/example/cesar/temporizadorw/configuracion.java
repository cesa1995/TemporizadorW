package com.example.cesar.temporizadorw;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class configuracion extends AppCompatActivity{

    TextView time, date;
    ImageButton timeb, dateb;
    FloatingActionButton saveBtn;

    HashMap<String, String> relaycon = new HashMap<>();

    int horan;
    int minutosn;
    int segundosn;
    String diaWeekn;
    int dian;
    int mesn;
    int yearn;
    String urlsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        date = findViewById(R.id.et_mostrar_fecha_picker);
        time = findViewById(R.id.et_mostrar_hora_picker);
        timeb = findViewById(R.id.ib_obtener_hora);
        dateb = findViewById(R.id.ib_obtener_fecha);
        saveBtn = findViewById(R.id.saveBtn);

        new Getcon().execute();

        timeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hora = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(configuracion.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horan = hourOfDay;
                        minutosn = minute;
                        time.setText(horan+":"+minutosn+":"+segundosn);
                    }
                },hora,min,true);
                timePickerDialog.show();
            }
        });

        dateb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int mes = c.get(Calendar.MONTH);
                Log.e(TAG,"el mes es "+mes);
                int year = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(configuracion.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dian = dayOfMonth;
                        mesn = month+1;
                        yearn = year;
                        date.setText(dian+"/"+mesn+"/"+yearn);
                    }
                },year,mes,dia);
                datePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                urlsave = "http://192.168.4.1/config?tconfig=1&ssid=stiotca&pass=1234567";
                urlsave = urlsave+"&hora="+horan+"&min="+minutosn+"&seg="+segundosn;
                urlsave = urlsave+"&dia="+dian+"&mes="+mesn+"&year="+yearn;

                new solicitarDatos().execute(urlsave);

                urlsave = "";

                new Getcon().execute();

            }
        });

    }

    public class Getcon extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                    JSONArray infocon = jsonObj.getJSONArray("infoconfig");
                    /*if (!relaycon.isEmpty()) {
                        //for (int i = 0; i < relayList.size(); i++) {
                        //  relayList.remove(i);
                        //}
                        relaycon.clear();
                    }*/
                    // looping through All Times
                    for (int i = 0; i < infocon.length(); i++) {
                        JSONObject c = infocon.getJSONObject(i);
                        String hora = c.getString("hora");
                        String minutos = c.getString("minutos");
                        String segundos = c.getString("segundos");
                        String diaWeek = c.getString("diaWeek");
                        String dia = c.getString("dia");
                        String mes = c.getString("mes");
                        String year = c.getString("year");
                        // tmp hash map for single Times

                        // adding each child node to HashMap key => value
                        relaycon.put("hora", hora);
                        relaycon.put("minutos", minutos);
                        relaycon.put("segundos", segundos);
                        relaycon.put("diaWeek", diaWeek);
                        relaycon.put("dia", dia);
                        relaycon.put("mes", mes);
                        relaycon.put("year", year);

                        horan = Integer.parseInt(relaycon.get("hora"));
                        minutosn = Integer.parseInt(relaycon.get("minutos"));
                        segundosn = Integer.parseInt(relaycon.get("segundos"));
                        diaWeekn = relaycon.get("diaWeek");
                        dian = Integer.parseInt(relaycon.get("dia"));
                        mesn = Integer.parseInt(relaycon.get("mes"));
                        yearn = Integer.parseInt(relaycon.get("year"));

                        // adding contact to contact list
//                        relayListcon.add(relaycon);

//                        System.out.println(relayListcon.toString());

                        //entryArrayList= new ArrayList<>(relay.entrySet());
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            time.setText(horan+":"+minutosn+":"+segundosn);
            date.setText(dian+"/"+mesn+"/"+yearn+"  "+diaWeekn);
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
                Toast.makeText(getApplicationContext(), resultado,Toast.LENGTH_SHORT).show();;
            }else {
                Toast.makeText(getApplicationContext(), "error de conexion", Toast.LENGTH_LONG).show();
            }
        }
    }


}
