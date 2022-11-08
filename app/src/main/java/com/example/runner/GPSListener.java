//package com.example.runner;
//
//import android.location.GpsStatus;
//import android.os.SystemClock;
//
//public class GPSListener implements GpsStatus.Listener {
//    @Override
//    public void onGpsStatusChanged(int event) {
//        switch (event) {
//            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                if (lastLocation != null) {
//                    isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
//                }
//        }
//    }
//}
