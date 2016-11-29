package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ch.fhnw.ip5.emotionhunt.helper.DbHelper;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class LocationHistory {
    //TODO remove entries from db after 500 inserts !

    public double lat;
    public double lon;
    int createdAt;

    /**
     * Returns the latest stored position in sql db.
     * @param context
     * @return
     */
    public static LocationHistory getLastPositionHistory(Context context) {
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + LocationDbContract.TABLE_NAME + " ORDER BY " +
                Experience.ExperienceDbContract.COL_CREATED_AT + " DESC LIMIT 1", null);
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

    public static LocationHistory loadFromCursor (Cursor c, LocationHistory l) {
        l.lat = c.getDouble(c.getColumnIndex(Experience.ExperienceDbContract.COL_LAT));
        l.lon = c.getDouble(c.getColumnIndex(Experience.ExperienceDbContract.COL_LON));
        l.createdAt = c.getInt(c.getColumnIndex(Experience.ExperienceDbContract.COL_CREATED_AT));
        return l;
    }

    public static void dummyInsert(Context context) {
        //TODO REMOVE THIS
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationDbContract.COL_LAT, 41.00);
        locationValues.put(LocationDbContract.COL_LON, 41.00);
        locationValues.put(LocationDbContract.COL_CREATED_AT, System.currentTimeMillis() / 1000L);
        db.insert(LocationDbContract.TABLE_NAME, null, locationValues);
    }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    public static abstract class LocationDbContract {
        public static final String TABLE_NAME = "location_history";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
        public static final String COL_CREATED_AT = "created_at";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (\n" +
                        "  `" + COL_CREATED_AT + "` INT NOT NULL,\n" +
                        "  `" + COL_LAT + "` DECIMAL(10,8) NOT NULL,\n" +
                        "  `" + COL_LON + "` DECIMAL(11,8) NOT NULL,\n" +
                        "  PRIMARY KEY (`" + COL_CREATED_AT + "`))\n";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SQL_COUNT_ITEMS = "SELECT COUNT(*) FROM " + TABLE_NAME;

    }
}
