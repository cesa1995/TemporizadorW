package com.example.cesar.temporizadorw.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cesar.temporizadorw.R;
import com.example.cesar.temporizadorw.comunication;

public class temperatura extends Fragment {

    int relay;
    comunication mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temperatura, container, false);
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        relay = 0;
        mCallback.sendata(2,relay);

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