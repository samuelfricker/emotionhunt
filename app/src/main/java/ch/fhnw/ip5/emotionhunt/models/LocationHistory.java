package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.services.LocationService;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class LocationHistory {
    public static final String TAG = "LocationHistory";

    public double lat;
    public double lon;
    public String provider;
    public float accuracy;
    int createdAt;

    /**
     * Returns the latest stored position in sql db.
     * @param context
     * @param provider use empty string for not using this criteria
     * @return
     */
    public static LocationHistory getLastPositionHistory(Context context, String provider) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + LocationDbContract.TABLE_NAME +
                " WHERE " + LocationDbContract.COL_PROVIDER + " LIKE '%" + provider + "%'" +
                " ORDER BY " +
                LocationDbContract.COL_CREATED_AT + " DESC LIMIT 1", null);
        if(c.moveToFirst()){
            do{
                LocationHistory locationHistory = new LocationHistory();
                locationHistory = loadFromCursor(c, locationHistory);
                c.close();
                db.close();
                return locationHistory;
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return null;
    }

    public boolean saveDb(Context context) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationHistory.LocationDbContract.COL_LAT, lat);
        locationValues.put(LocationHistory.LocationDbContract.COL_LON, lon);
        locationValues.put(LocationHistory.LocationDbContract.COL_PROVIDER, provider);
        locationValues.put(LocationHistory.LocationDbContract.COL_ACCURACY, accuracy);
        locationValues.put(LocationHistory.LocationDbContract.COL_CREATED_AT, System.currentTimeMillis() / 1000L);

        boolean validation = db.insertWithOnConflict(LocationHistory.LocationDbContract.TABLE_NAME, null, locationValues, SQLiteDatabase.CONFLICT_IGNORE) != -1;

        if (validation) {
            Log.d(TAG, String.format("location from %1$s with an accuracy about %2$.3f stored into sql lite db.", provider, accuracy));
            LocationService.cleanUpEntries(db);
        } else {
            db.close();
            Log.d(TAG, "location couldn't be stored into sql lite db.");
        }

        return validation;
    }

    public static LocationHistory loadFromCursor (Cursor c, LocationHistory l) {
        l.lat = c.getDouble(c.getColumnIndex(LocationDbContract.COL_LAT));
        l.lon = c.getDouble(c.getColumnIndex(LocationDbContract.COL_LON));
        l.provider = c.getString(c.getColumnIndex(LocationDbContract.COL_PROVIDER));
        l.accuracy = c.getFloat(c.getColumnIndex(LocationDbContract.COL_ACCURACY));
        l.createdAt = c.getInt(c.getColumnIndex(LocationDbContract.COL_CREATED_AT));
        return l;
    }

    public Location getLocation() {
        Location location = new Location("dummyprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setProvider(provider);
        location.setAccuracy(accuracy);
        return location;
    }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    public static abstract class LocationDbContract {
        public static final String TABLE_NAME = "location_history";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
        public static final String COL_PROVIDER = "provider";
        public static final String COL_ACCURACY = "accuracy";
        public static final String COL_CREATED_AT = "created_at";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (\n" +
                        "  `" + COL_CREATED_AT + "` INT NOT NULL,\n" +
                        "  `" + COL_LAT + "` DECIMAL(10,8) NOT NULL,\n" +
                        "  `" + COL_LON + "` DECIMAL(11,8) NOT NULL,\n" +
                        "  `" + COL_PROVIDER + "` VARCHAR(255) NOT NULL,\n" +
                        "  `" + COL_ACCURACY + "` DECIMAL(8,3) NOT NULL,\n" +
                        "  PRIMARY KEY (`" + COL_CREATED_AT + "`))\n";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SQL_COUNT_ITEMS = "SELECT COUNT(*) FROM " + TABLE_NAME;
        public static final String SQL_DELETE_LAST_50 = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_CREATED_AT +
                " IN (SELECT " + COL_CREATED_AT + " FROM " + TABLE_NAME + " ORDER BY " + COL_CREATED_AT + " ASC LIMIT 50)";
    }
}
