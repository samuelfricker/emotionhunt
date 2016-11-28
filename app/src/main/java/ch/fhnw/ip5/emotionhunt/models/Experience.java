package ch.fhnw.ip5.emotionhunt.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public abstract class Experience{

    public abstract Experience findById(SQLiteDatabase db, int id);
    public static void loadExperiencesFromApi(Context context, boolean isPublic) { }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    public static abstract class ExperienceDbContract {
        public static final String TABLE_NAME = "experience";
        public static final String COL_ID = "id";
        public static final String COL_IS_SENT = "is_sent";
        public static final String COL_IS_PUBLIC = "is_public";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_VISIBILITY_DURATION = "visibility_duration";
        public static final String COL_TEXT = "text";
        public static final String COL_FILENAME = "filename";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS`" + TABLE_NAME +"` (\n" +
                        "  `" + COL_ID + "` INT NOT NULL,\n" +
                        "  `" + COL_IS_SENT + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_PUBLIC + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_LAT + "` DECIMAL(10,8) NOT NULL,\n" +
                        "  `" + COL_LON + "` DECIMAL(11,8) NOT NULL,\n" +
                        "  `" + COL_CREATED_AT + "` INT NOT NULL,\n" +
                        "  `" + COL_VISIBILITY_DURATION + "` INT NULL,\n" +
                        "  `" + COL_TEXT + "` TEXT NULL,\n" +
                        "  `" + COL_FILENAME + "` VARCHAR(255) NULL,\n" +
                        "  PRIMARY KEY (`" + COL_ID + "`))";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
