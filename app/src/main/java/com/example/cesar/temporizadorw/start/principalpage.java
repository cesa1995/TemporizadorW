package com.example.cesar.temporizadorw.start;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.cesar.temporizadorw.show;

public class principalpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=new Intent(this,show.class);
        startActivity(intent);
        finish();
    }
}
