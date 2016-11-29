package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.helper.DbHelper;
import ch.fhnw.ip5.emotionhunt.helper.PermissionHelper;

/**
 * Created by dimitri on 28.11.2016.
 */

public class GPSTracker extends ContextCompat implements LocationListener {

    private static final String TAG = GPSTracker.class.getSimpleName();
    //the minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    //the minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;
    private final Context mContext;
    Location location;
    //declaring a Location Manager
    protected LocationManager locationManager;

    /**
     * C'tor
     * @param mContext
     */
    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        try {
            initLocationListener();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Initializes the LocationListener.
     * @throws Exception
     */
    public void initLocationListener() throws Exception {
        if (PermissionHelper.checkLocationPermission(mContext)) {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);

            //try go get last known position first
            if (locationManager != null) {
                this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                insertLocation();
            }
            Log.d(TAG, "Listener initialized");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (PermissionHelper.checkLocationPermission(mContext)) {
            this.location = location;
            Log.i(TAG, "Location " + location.getLatitude() + ", " + location.getLongitude());
            insertLocation();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged " + status);
    }

    /**
     * Inserts a new location entry into sql lite db.
     * @return successful state
     */
    public boolean insertLocation () {
        if (location == null) return false;

        SQLiteDatabase db = new DbHelper(mContext).getWritableDatabase();
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationHistory.LocationDbContract.COL_LAT, location.getLatitude());
        locationValues.put(LocationHistory.LocationDbContract.COL_LON, location.getLongitude());
        locationValues.put(LocationHistory.LocationDbContract.COL_CREATED_AT, System.currentTimeMillis() / 1000L);
        boolean validation = db.insert(LocationHistory.LocationDbContract.TABLE_NAME, null, locationValues) != -1;

        if (validation) {
            Log.d(TAG, "location stored into sql lite db.");
        } else {
            Log.d(TAG, "location couldn't be stored into sql lite db.");
        }

        return validation;
    }
}
