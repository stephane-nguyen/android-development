package com.tps.tp2nguyenstephane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



public class ColorActivity extends AppCompatActivity {

    private static final String BLUE = "#0000FF";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        Intent intentToTransportData = new Intent();
        intentToTransportData.putExtra("color", BLUE);
        setResult(Activity.RESULT_OK, intentToTransportData);
        finish();
    }

}