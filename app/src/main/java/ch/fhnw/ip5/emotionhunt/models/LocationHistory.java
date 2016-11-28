package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ch.fhnw.ip5.emotionhunt.helper.DbHelper;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class LocationHistory {

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
    }
}
