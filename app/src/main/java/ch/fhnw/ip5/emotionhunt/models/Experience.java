package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.Telephony;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.annotations.SerializedName;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.activities.MainActivity;
import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */

public abstract class Experience{
    private static final String TAG = Experience.class.getSimpleName();
    private static final int CATCHABLE_WITHIN_METERS = 50;

    @SerializedName("id")
    public long id;
    @SerializedName("is_sent")
    public boolean isSent;
    @SerializedName("is_public")
    public int isPublicApi;
    public boolean isPublic;
    public boolean isRead;
    public double lat;
    public double lon;
    @SerializedName("created_at")
    public int createdAt;
    @SerializedName("visibility_duration")
    public int visibilityDuration;
    public String text;
    public String filename;
    public String emotion;

    public static Experience findById(Context context, long id) { return null; };
    public static void loadExperiencesFromApi(Context context, boolean isPublic) { }

    public abstract boolean saveDb (Context context);

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual = false;
        if (object != null && object instanceof Experience) {
            isEqual = this.id == ((Experience) object).id;
        }
        return isEqual;
    }

    /**
     * Loads all params from a cursor into a given Experience Instance.
     * @param c Cursor (sql lite row)
     * @param e Experience instance
     * @return Experience Object with loaded attributes
     */
    public static Experience loadFromCursor (Cursor c, Experience e) {
        e.id = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_ID));
        e.lat = c.getDouble(c.getColumnIndex(ExperienceDbContract.COL_LAT));
        e.lon = c.getDouble(c.getColumnIndex(ExperienceDbContract.COL_LON));
        e.isSent = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_IS_SENT)) == 1;
        e.isPublic = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_IS_PUBLIC)) == 1;
        e.isRead = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_IS_READ)) == 1;
        e.createdAt = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_CREATED_AT));
        e.visibilityDuration = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_VISIBILITY_DURATION));
        e.text = c.getString(c.getColumnIndex(ExperienceDbContract.COL_TEXT));
        e.filename = c.getString(c.getColumnIndex(ExperienceDbContract.COL_FILENAME));
        e.emotion = c.getString(c.getColumnIndex(ExperienceDbContract.COL_EMOTION));
        return e;
    }

    /**
     * Returns the experience's Location object
     * @return
     */
    public Location getLocation() {
        Location location = new Location("dummyprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }

    /**
     * @return whether this experience is read or not
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Returns the marker icon
     * @return
     */
    public BitmapDescriptor getMarkerIcon(Context context) {
        if(this.isRead()){
            return BitmapDescriptorFactory.fromResource(R.drawable.img_location);
        }else if(this.isCatchable(context)){
            return BitmapDescriptorFactory.fromResource(R.drawable.img_location_checked);
        } else if (!this.isRead() && !this.isCatchable(context)){
            return BitmapDescriptorFactory.fromResource(R.drawable.img_location_cross);
        } else{
            return BitmapDescriptorFactory.fromResource(R.drawable.img_location);
        }
    }

    /**
     * Updates an existing experience as isRead
     * @param context
     * @return
     */
    public boolean updateIsRead (Context context) {
        Log.d(TAG, "updateIsRead");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExperienceDbContract.COL_IS_READ, 1);
        return db.update(ExperienceDbContract.TABLE_NAME, contentValues, "id=" + id, null) != -1;
    }

    public boolean updateEmotion (Context context) {
        Log.d(TAG, "updateEmotion");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExperienceDbContract.COL_EMOTION, emotion);
        return db.update(ExperienceDbContract.TABLE_NAME, contentValues, "id=" + id, null) != -1;
    }

    public boolean isPrivate() {
        return !isPublic;
    }

    /**
     * Returns whether an experience is ready to be opened or not.
     * That depends on the "isRead" and "isSent" and on the distance.
     * @return whether this experience is catchable or not
     */
    public boolean isCatchable(Context context) {
        //read or sent experiences are not catchable because they are already catched or self-created
        if (isRead || this instanceof SentExperience) return false;

        //validate distance from the experience and the current location
        LocationHistory currentLocation = LocationHistory.getLastPositionHistory(context);
        return currentLocation.getLocation().distanceTo(getLocation()) < CATCHABLE_WITHIN_METERS;
    }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    public static abstract class ExperienceDbContract {
        public static final String TABLE_NAME = "experience";
        public static final String COL_ID = "id";
        public static final String COL_IS_SENT = "is_sent";
        public static final String COL_IS_PUBLIC = "is_public";
        public static final String COL_IS_READ = "is_read";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_VISIBILITY_DURATION = "visibility_duration";
        public static final String COL_TEXT = "text";
        public static final String COL_FILENAME = "filename";
        public static final String COL_EMOTION = "emotion";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS`" + TABLE_NAME +"` (\n" +
                        "  `" + COL_ID + "` INT NOT NULL,\n" +
                        "  `" + COL_IS_SENT + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_PUBLIC + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_READ + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_LAT + "` DECIMAL(10,8) NOT NULL,\n" +
                        "  `" + COL_LON + "` DECIMAL(11,8) NOT NULL,\n" +
                        "  `" + COL_CREATED_AT + "` INT NOT NULL,\n" +
                        "  `" + COL_VISIBILITY_DURATION + "` INT NULL,\n" +
                        "  `" + COL_TEXT + "` TEXT NULL,\n" +
                        "  `" + COL_EMOTION + "` TEXT NULL,\n" +
                        "  `" + COL_FILENAME + "` VARCHAR(255) NULL,\n" +
                        "  PRIMARY KEY (`" + COL_ID + "`))";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SQL_COUNT_ITEMS = "SELECT COUNT(*) FROM " + TABLE_NAME;
    }
}
