package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class TrainingProcess extends AppCompatActivity implements LocListenerInterface {

    private long milliSec, tStart, tBuff, tUpdate = 0L;
    private boolean running;
    private Handler handler;
    private Chronometer stopWatch;
    private ImageButton buttonResume;
    private ImageButton buttonStop;

    private LocationManager locationManager;
    private TextView tvDistance;
    private Location lastLocation;
    private MyLocListener myLocListener;
    private int distance;

    private TextView tvCurrentPace;
    private TextView tvAvgPace;
    private double speed;
    private double avg_speed = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_process);
        init();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        stopWatch = findViewById(R.id.chronometer);
        buttonResume = findViewById(R.id.Resume);
        buttonStop = findViewById(R.id.StopTraining);
        buttonStop.setVisibility(View.GONE);
        buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));

        tvDistance = findViewById(R.id.tvDistance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(this);
        requestPermission();

        running = true;
        handler = new Handler();
        tStart = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        tvCurrentPace = findViewById(R.id.currentPace);
        tvAvgPace = findViewById(R.id.avgPace);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickResumeTraining(View view) {
        // Resume
        if (!running) {
            tStart = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            running = true;
            buttonStop.setVisibility(View.GONE);
            buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            // Pause
        } else {
            tBuff += milliSec;
            handler.removeCallbacks(runnable);
            stopWatch.stop();
            running = false;
            buttonStop.setVisibility(View.VISIBLE);
            buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_resume));
        }
    }

    public void onClickStopTraining(View view) {
        finish();
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + milliSec;
            int allSeconds = (int) (tUpdate / 1000);
            int hours = allSeconds / 3600;
            int minutes = (allSeconds % 3600) / 60;
            int seconds = allSeconds % 60;

            String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
            stopWatch.setText(time);
            handler.postDelayed(this, 60);
        }
    };

    @SuppressLint("MissingPermission")
    private void requestPermission() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, myLocListener);
    }

    @Override
    public void OnLocationChanged(Location loc) {
        if (running) {
            if (loc.hasSpeed() && lastLocation != null) {
                distance += lastLocation.distanceTo(loc);
                speed = 50 / (3 * loc.getSpeed());
                avg_speed = avg_speed == -1 ? speed : (avg_speed + speed) / 2;
                int kilometers = distance / 1000;
                int meters = distance % 1000 / 10;
                String text_distance = String.format(Locale.getDefault(), "%d.%02d", kilometers, meters);
                tvDistance.setText(text_distance);
                int speed_min = (int) speed;
                int speed_sec = (int) ((speed - speed_min) * 60);
                int avg_speed_min = (int) avg_speed;
                int avg_speed_sec = (int) ((avg_speed - avg_speed_min) * 60);
                String text_speed = String.format(Locale.getDefault(), "%d.%02d", speed_min, speed_sec);
                String text_avg_speed = String.format(Locale.getDefault(), "%d.%02d", avg_speed_min, avg_speed_sec);
                tvCurrentPace.setText(text_speed);
                tvAvgPace.setText(text_avg_speed);
            }
            lastLocation = loc;
        } else {
            tvCurrentPace.setText("--:--");
        }
    }
}