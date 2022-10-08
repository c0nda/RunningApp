package com.example.runner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
    private ImageButton buttonPause;
    private ImageButton buttonReset;

    private LocationManager locationManager;
    private TextView tvDistance;
    private Location lastLocation;
    private MyLocListener myLocListener;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_process);
        init();
    }

    private void init() {
        stopWatch = findViewById(R.id.chronometer);
        buttonPause = findViewById(R.id.Pause);
//        buttonReset = findViewById(R.id.Reset);

        handler = new Handler();
        running = true;
        tStart = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        tvDistance = findViewById(R.id.tvDistance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(this);
        checkPermission();
    }

//    public void onClickStartTraining(View view) {
//        if (!running) {
//            Intent intent = new Intent(this, TrainingProcess.class);
//            startActivity(intent);
//            tStart = SystemClock.uptimeMillis();
//            handler.postDelayed(runnable, 0);
//            running = true;
//        }
//    }

    public void onClickStopTraining(View view) {
        tBuff += milliSec;
        handler.removeCallbacks(runnable);
        stopWatch.stop();
        running = false;
    }

//    public void onClickReset(View view) {
//        if (!running) {
//            milliSec = 0L;
//            tStart = 0L;
//            tBuff = 0L;
//            tUpdate = 0L;
//            stopWatch.setText("0:00:00");
//        }
//    }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK) {
            checkPermission();
        }
//        else {
//            Toast.makeText(this, "No GPS permission", Toast.LENGTH_SHORT).show();
//        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, myLocListener);
        }
    }

    @Override
    public void OnLocationChanged(Location loc) {
        if (loc.hasSpeed() && lastLocation != null) {
            distance += lastLocation.distanceTo(loc);
        }
        lastLocation = loc;
        tvDistance.setText(String.valueOf(distance));
    }
}