package ch.fhnw.ip5.emotionhunt.models;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public class Experience{

    public Experience findById(SQLiteDatabase db, int id) {
        return null;
    }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    /*public static abstract class ParticipationDbContract implements BaseColumns {
        public static final String TABLE_NAME = "Participation";
        public static final String _ID = "id";
        public static final String COL_EVENT_ID = "event_id";
        public static final String COL_DRIVER_PERSON_ID = "driver_person_id";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " + COL_EVENT_ID + " INTEGER, " +
                        COL_DRIVER_PERSON_ID + " INTEGER)";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }*/
}
