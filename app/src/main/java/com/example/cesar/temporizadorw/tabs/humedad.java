package com.example.cesar.temporizadorw.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import com.example.cesar.temporizadorw.comunication;

import com.example.cesar.temporizadorw.R;

public class humedad extends Fragment {

    TextView relayName,humedadvalue,huminicial,humfinal,ontime,oftime;
    FloatingActionButton saveBtn;
    Switch humedadswi;
    ImageButton huminicialbtn,humfinalbtn,ontimebtn,oftimebtn;
    NumberPicker hora, minutos, segundos;
    int relay;
    comunication mCallback;


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

        mCallback.sendata(1,relay);




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
