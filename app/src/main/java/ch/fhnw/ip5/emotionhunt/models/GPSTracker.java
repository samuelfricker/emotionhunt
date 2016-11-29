package ch.fhnw.ip5.emotionhunt.models;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by dimitri on 28.11.2016.
 */

public class GPSTracker extends AppCompatActivity implements LocationListener {

    private static final String TAG = GPSTracker.class.getSimpleName();
    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;


    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        try {
            getLocation();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public Location getLocation() throws Exception {

        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.i(TAG, "Network Provider & GPS is not enabled");
            } else {
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);
                    Log.i(TAG, "Conected To GPS Provider");
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                } else {
                    Log.i(TAG, "Network GPS is Disabled");
                }
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage()+" "+ e.toString());
        }

        return location;

    }



    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }





    public void updateLocation() {
        try{
            Location current = getLocation();
            Log.i(TAG, "Altitude: "+current.getAltitude()+" Longitude: "+current.getLongitude());
        } catch (Exception e){
            Log.i(TAG, e.getMessage());
        }

        /*
        SQLiteDatabase db = new DbHelper(mContext).getWritableDatabase();
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationDbContract.COL_LAT, 41.00);
        locationValues.put(LocationDbContract.COL_LON, 41.00);
        locationValues.put(LocationDbContract.COL_CREATED_AT, System.currentTimeMillis() / 1000L);
        db.insert(LocationDbContract.TABLE_NAME, null, locationValues);
        */
    }






}
