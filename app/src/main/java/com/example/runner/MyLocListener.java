package com.example.runner;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocListener implements LocationListener {
    private LocListenerInterface locListenerInterface;

    @Override
    public void onLocationChanged(Location location) {
        locListenerInterface.OnLocationChanged((location));
    }

    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}
