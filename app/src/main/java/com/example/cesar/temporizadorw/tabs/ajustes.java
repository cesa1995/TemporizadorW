package com.example.cesar.temporizadorw.tabs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cesar.temporizadorw.R;
import com.example.cesar.temporizadorw.conexion;
import com.example.cesar.temporizadorw.configuracion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ajustes extends Fragment{

    TextView time, date;
    ImageButton timeb, dateb;
    FloatingActionButton saveBtn;

    HashMap<String, String> relaycon = new HashMap<>();

    int horan, minutosn, segundosn, dian, mesn, yearn;
    String diaWeekn, urlsave;
    int hora, min, dia, mes, year, seg;
    SharedPreferences data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ajustes, container, false);
       initUI(v);
       return v;
    }


    private void initUI(View v) {


        date = v.findViewById(R.id.et_mostrar_fecha_picker);
        time = v.findViewById(R.id.et_mostrar_hora_picker);
        timeb = v.findViewById(R.id.ib_obtener_hora);
        dateb = v.findViewById(R.id.ib_obtener_fecha);
        saveBtn = v.findViewById(R.id.saveBtn);
        data = this.getActivity().getSharedPreferences("datosDevice", Context.MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        hora = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        seg = c.get(Calendar.SECOND);
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        new Getcon().execute();

        timeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horan = hourOfDay;
                        minutosn = minute;
                        time.setText(horan + ":" + minutosn + ":" + segundosn);
                    }
                }, hora, min, true);
                timePickerDialog.show();
            }
        });

        dateb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dian = dayOfMonth;
                        mesn = month + 1;
                        yearn = year;
                        date.setText(dian + "/" + mesn + "/" + yearn);
                    }
                }, year, mes, dia);
                datePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
            horan = Integer.parseInt(data.getString("hora", "no Data"));
            minutosn = Integer.parseInt(data.getString("minutos","no Data"));
            segundosn = Integer.parseInt(data.getString("segundos","no Data"));
            diaWeekn = data.getString("diaweek", "no Data");
            dian = Integer.parseInt(data.getString("dia", "no Data"));
            mesn = Integer.parseInt(data.getString("mes", "no Data"));
            yearn = Integer.parseInt(data.getString("year", "no Data"));
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
                Toast.makeText(getContext(), resultado,Toast.LENGTH_SHORT).show();;
            }else {
                Toast.makeText(getContext(), "error de conexion", Toast.LENGTH_LONG).show();
            }
        }
    }


}