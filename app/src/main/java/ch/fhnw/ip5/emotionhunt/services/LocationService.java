package ch.fhnw.ip5.emotionhunt.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.PermissionHelper;
import ch.fhnw.ip5.emotionhunt.models.LocationHistory;


/**
 * Created by dimitri on 28.11.2016.
 */

public class LocationService extends Service implements LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();
    //the minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    //the minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;
    private Context mContext;
    Location location;
    //declaring a Location Manager
    protected LocationManager locationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        try {
            initLocationListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
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
                LocationService.insertLocation(location, mContext);
            }
            Log.d(TAG, "Listener initialized");
        }
    }

    /**
     * Inserts a new location entry into sql lite db.
     * @return successful state
     */
    public static boolean insertLocation (Location loc, Context context) {
        if (loc == null) return false;

        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationHistory.LocationDbContract.COL_LAT, loc.getLatitude());
        locationValues.put(LocationHistory.LocationDbContract.COL_LON, loc.getLongitude());
        locationValues.put(LocationHistory.LocationDbContract.COL_CREATED_AT, System.currentTimeMillis() / 1000L);

        boolean validation = db.insertWithOnConflict(LocationHistory.LocationDbContract.TABLE_NAME, null, locationValues, SQLiteDatabase.CONFLICT_IGNORE) != -1;

        if (validation) {
            Log.d(TAG, "location stored into sql lite db.");
            LocationService.cleanUpEntries(db);
        } else {
            db.close();
            Log.d(TAG, "location couldn't be stored into sql lite db.");
        }

        return validation;
    }

    /**
     * Delete last 50 entries if there are more than 80 entries stored in sqlite db.
     * @param db
     */
    public static void cleanUpEntries(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(LocationHistory.LocationDbContract.SQL_COUNT_ITEMS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count > 80) {
            db.execSQL(LocationHistory.LocationDbContract.SQL_DELETE_LAST_50);
        }
        db.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (PermissionHelper.checkLocationPermission(mContext)) {
            this.location = location;
            Log.i(TAG, "Location " + location.getLatitude() + ", " + location.getLongitude());
            insertLocation(location, mContext);
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

}
