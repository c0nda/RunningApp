package com.example.runner;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements LocListenerInterface {
    private LinearLayout mainButton;
    private ImageButton buttonStart;
    private TextView tvGpsStatus;
    private TextView tvGpsProviderStatus;

    private long milliSec, tStart, tBuff, tUpdate = 0L;
    private boolean running = false;
    private Handler handler;
    private Chronometer stopWatch;
    private ImageButton buttonResume;
    private ImageButton buttonStop;

    private LocationManager locationManager;
    private TextView tvDistance;
    private Location lastLocation;
    private LocListener locListener;
    private int distance;

    private TextView tvCurrentPace;
    private TextView tvAvgPace;
    private double avg_speed = -1;

    private GpsStatus.Listener gpsStatusListener;
    private boolean isGPSFix;
    private long lastLocationMillis;

    private ImageButton buttonTimer;
    private ImageButton buttonTraining;
    private ImageButton buttonProfile;
    private LinearLayout staticDownBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEnabled();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        buttonStart = findViewById(R.id.StartTraining);
        mainButton = findViewById(R.id.linearLayout);
        tvGpsStatus = findViewById(R.id.tvGpsStatus);

        stopWatch = findViewById(R.id.chronometer);
        buttonResume = findViewById(R.id.Resume);
        buttonStop = findViewById(R.id.StopTraining);

        buttonStop.setVisibility(View.GONE);
        buttonResume.setVisibility(View.GONE);

        tvDistance = findViewById(R.id.tvDistance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new LocListener();
        locListener.setLocListenerInterface(this);
        checkPermission();

        tvCurrentPace = findViewById(R.id.currentPace);
        tvAvgPace = findViewById(R.id.avgPace);

        buttonTimer = findViewById(R.id.buttonTimer);
        buttonTraining = findViewById(R.id.buttonTraining);
        buttonProfile = findViewById(R.id.buttonProfile);
        staticDownBar = findViewById(R.id.staticDownBar);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onClickStartTraining(View view) {
        if (!running) {
            running = true;
            handler = new Handler();
            tStart = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);

            mainButton.setVisibility(View.GONE);
            buttonResume.setVisibility(View.VISIBLE);
            buttonResume.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            staticDownBar.setVisibility(View.GONE);
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
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

    @SuppressLint("SetTextI18n")
    public void onClickStopTraining(View view) {
        locationManager.removeUpdates(locListener);
        milliSec = 0;
        tStart = 0;
        tBuff = 0;
        tUpdate = 0;
        avg_speed = -1;
        distance = 0;
        lastLocation = null;
        stopWatch.setText("0:00:00");
        tvDistance.setText("0.00");
        tvAvgPace.setText("--:--");
        tvCurrentPace.setText("--:--");
        buttonResume.setVisibility(View.GONE);
        buttonStop.setVisibility(View.GONE);
        mainButton.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).show();
        staticDownBar.setVisibility(View.VISIBLE);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK) {
            checkPermission();
            buttonStart.setClickable(false);
            mainButton.setBackground(getResources().getDrawable(R.drawable.circle_button_darker_gray));
        } else {
            buttonStart.setClickable(true);
            mainButton.setBackground(getResources().getDrawable(R.drawable.circle_button));
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locListener);
//            gpsStatusListener = new GpsStatus.Listener() {
//                @Override
//                public void onGpsStatusChanged(int event) {
//                    switch (event) {
//                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                            if (lastLocation != null) {
//                                isGPSFix = (SystemClock.elapsedRealtime() - lastLocationMillis) < 3000;
//                            }
//                            if (isGPSFix) {
//                                tvGpsStatus.setText("1");
//                            } else {
//                                tvGpsStatus.setText("0");
//                            }
//                            break;
//                        case GpsStatus.GPS_EVENT_FIRST_FIX:
//                            isGPSFix = true;
//                            tvGpsStatus.setText("1");
//                            break;
//                    }
//                }
//            };
//            locationManager.addGpsStatusListener(gpsStatusListener);
        }
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

    @Override
    public void OnLocationChanged(Location loc) {
//        lastLocation = loc;
//        lastLocationMillis = SystemClock.elapsedRealtime();
        if (running) {
            if (loc.hasSpeed() && lastLocation != null) {
                distance += lastLocation.distanceTo(loc);
                double speed = 50 / (3 * loc.getSpeed());
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
            lastLocationMillis = SystemClock.elapsedRealtime();
        } else {
            tvCurrentPace.setText("--:--");
        }
    }

    @Override
    public void OnProviderEnabled(String provider) {
        checkEnabled();
    }

    @Override
    public void OnProviderDisabled(String provider) {
        checkEnabled();
    }

    private void checkEnabled() {
        tvGpsStatus.setText(String.format("GPS: %s", locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public void onClickButtonTimer(View view) {
        Intent intent = new Intent(this, IntervalTimer.class);
        startActivity(intent);
        finish();
    }
}

