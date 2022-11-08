package com.example.runner;

import android.location.Location;
import android.location.LocationListener;

public class LocListener implements LocationListener {
    private LocListenerInterface locListenerInterface;

    @Override
    public void onLocationChanged(Location location) {
        locListenerInterface.OnLocationChanged((location));
    }

    @Override
    public void onProviderEnabled(String provider) {
        locListenerInterface.OnProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        locListenerInterface.OnProviderDisabled(provider);
    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}
