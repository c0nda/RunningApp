package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    SharedPreferences isFirstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Handler handler = new Handler();
        Intent intent;

        isFirstStart = getSharedPreferences("FirstStart", MODE_PRIVATE);
        if (isFirstStart.getInt("isFirstStart", 0) == 1) {
            intent = new Intent(this, TrainingProcess.class);
        } else {
            intent = new Intent(this, PersonParametersFirstStart.class);
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}

