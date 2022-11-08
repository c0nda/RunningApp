package com.example.runner;

import android.location.Location;

public interface LocListenerInterface {
    void OnLocationChanged(Location loc);

    void OnProviderEnabled(String provider);

    void OnProviderDisabled(String provider);
}
