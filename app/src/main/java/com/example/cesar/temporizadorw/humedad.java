package com.example.cesar.temporizadorw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;

public class humedad extends Fragment {

    ToggleButton I,II,III,IV;
    TextView relayName,humedadvalue,huminicial,humfinal,ontime,oftime;
    FloatingActionButton saveBtn;
    Switch humedadswi;
    ImageButton huminicialbtn,humfinalbtn,ontimebtn,oftimebtn;
    NumberPicker hora, minutos, segundos;

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
        humedadvalue = v.findViewById(R.id.humedad);
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
        I = v.findViewById(R.id.I);
        II = v.findViewById(R.id.II);
        III = v.findViewById(R.id.III);
        IV = v.findViewById(R.id.IV);


    }

}