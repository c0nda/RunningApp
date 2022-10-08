package com.example.runner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    private ImageButton buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        buttonStart = findViewById(R.id.StartTraining);
    }

    public void onClickStartTraining(View view) {
        Intent intent = new Intent(this, TrainingProcess.class);
        startActivity(intent);
    }
}
//    public void onClickStopTraining(View view) {
//        tBuff += milliSec;
//        handler.removeCallbacks(runnable);
//        stopWatch.stop();
//        running = false;
//    }
//
//    public void onClickReset(View view) {
//        if (!running) {
//            milliSec = 0L;
//            tStart = 0L;
//            tBuff = 0L;
//            tUpdate = 0L;
//            stopWatch.setText("0:00:00");
//        }
//    }
//
//    public Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            milliSec = SystemClock.uptimeMillis() - tStart;
//            tUpdate = tBuff + milliSec;
//            int allSeconds = (int) (tUpdate / 1000);
//            int hours = allSeconds / 3600;
//            int minutes = (allSeconds % 3600) / 60;
//            int seconds = allSeconds % 60;
//
//            String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
//            stopWatch.setText(time);
//            handler.postDelayed(this, 60);
//        }
//    };
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100 && grantResults[0] == RESULT_OK) {
//            checkPermission();
//        }
////        else {
////            Toast.makeText(this, "No GPS permission", Toast.LENGTH_SHORT).show();
////        }
//    }
//
//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
//        } else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, myLocListener);
//        }
//    }
//
//    @Override
//    public void OnLocationChanged(Location loc) {
//        if (loc.hasSpeed() && lastLocation != null) {
//            distance += lastLocation.distanceTo(loc);
//        }
//        lastLocation = loc;
//        tvDistance.setText(String.valueOf(distance));
//    }

