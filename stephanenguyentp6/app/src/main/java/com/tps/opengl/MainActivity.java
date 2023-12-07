package com.tps.opengl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myView = new MyView(this);
        ConstraintLayout layout= findViewById(R.id.constraint_layout);
        layout.addView(myView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.pause();
    }
}
