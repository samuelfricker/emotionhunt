package ch.fhnw.ip5.emotionhunt.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.Params;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.models
 *
 * @author Benjamin Bur
 */
public abstract class Experience {
    private static final String TAG = Experience.class.getSimpleName();
    private static final int CATCHABLE_WITHIN_METERS = 50;

    public Emotion expectedEmotion;

    @SerializedName("id")
    public long id;
    @SerializedName("is_sent")
    public boolean isSent;
    @SerializedName("is_location_based")
    public int isLocationBasedApi;
    public boolean isLocationBased;
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
    @SerializedName("sender_name")
    public String senderName;
    @SerializedName("sender_id")
    public long senderId;

    /**
     * Stores the current instance into the local SQLite database.
     * Parent classes must implement this method.
     * @param context
     * @return either the save was successful or not
     */
    public abstract boolean saveDb (Context context);

    /**
     * Either a given object is equal to the current instance or not.
     * @param object object to compare with this
     * @return is equal
     */
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
        e.isLocationBased = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_IS_LOCATION_BASED)) == 1;
        e.isRead = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_IS_READ)) == 1;
        e.createdAt = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_CREATED_AT));
        e.visibilityDuration = c.getInt(c.getColumnIndex(ExperienceDbContract.COL_VISIBILITY_DURATION));
        e.text = c.getString(c.getColumnIndex(ExperienceDbContract.COL_TEXT));
        e.filename = c.getString(c.getColumnIndex(ExperienceDbContract.COL_FILENAME));
        e.emotion = c.getString(c.getColumnIndex(ExperienceDbContract.COL_EMOTION));
        e.senderId = c.getLong(c.getColumnIndex(ExperienceDbContract.COL_SENDER_ID));
        e.senderName = c.getString(c.getColumnIndex(ExperienceDbContract.COL_SENDER_NAME));
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

    /**
     * Updates an existing emotion on an experience.
     * @param context
     * @return
     */
    public boolean updateEmotion (Context context) {
        Log.d(TAG, "updateEmotion");
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExperienceDbContract.COL_EMOTION, emotion);
        return db.update(ExperienceDbContract.TABLE_NAME, contentValues, "id=" + id, null) != -1;
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
        LocationHistory currentLocation = LocationHistory.getLastPositionHistory(context,"fused");
        return currentLocation != null && currentLocation.getLocation().distanceTo(getLocation()) < CATCHABLE_WITHIN_METERS;
    }

    public String getCreatedAt() {
        Log.d(TAG, String.format("Created at: %1$s",createdAt));
        Date date = new java.util.Date(createdAt *1000L);
        return DateFormat.getDateTimeInstance().format(date);
    }

    public boolean isPrivate() {
        return !isPublic;
    }

    /**
     * This is the db contract to persist this model in the sqlite db.
     */
    public static abstract class ExperienceDbContract {
        public static final String TABLE_NAME = "experience";
        public static final String COL_ID = "id";
        public static final String COL_IS_SENT = "is_sent";
        public static final String COL_IS_PUBLIC = "is_public";
        public static final String COL_IS_LOCATION_BASED = "is_location_based";
        public static final String COL_IS_READ = "is_read";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_VISIBILITY_DURATION = "visibility_duration";
        public static final String COL_TEXT = "text";
        public static final String COL_FILENAME = "filename";
        public static final String COL_EMOTION = "emotion";
        public static final String COL_SENDER_ID = "sender_id";
        public static final String COL_SENDER_NAME = "sender_name";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS`" + TABLE_NAME +"` (\n" +
                        "  `" + COL_ID + "` INT NOT NULL,\n" +
                        "  `" + COL_IS_SENT + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_PUBLIC + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_READ + "` TINYINT(1) NOT NULL DEFAULT 0,\n" +
                        "  `" + COL_IS_LOCATION_BASED + "` TINYINT(1) NOT NULL DEFAULT 1,\n" +
                        "  `" + COL_LAT + "` DECIMAL(10,8) NOT NULL,\n" +
                        "  `" + COL_LON + "` DECIMAL(11,8) NOT NULL,\n" +
                        "  `" + COL_CREATED_AT + "` INT NOT NULL,\n" +
                        "  `" + COL_VISIBILITY_DURATION + "` INT NULL,\n" +
                        "  `" + COL_TEXT + "` TEXT NULL,\n" +
                        "  `" + COL_EMOTION + "` TEXT NULL,\n" +
                        "  `" + COL_SENDER_ID + "` INT NULL,\n" +
                        "  `" + COL_SENDER_NAME + "` VARCHAR(255) NULL,\n" +
                        "  `" + COL_FILENAME + "` VARCHAR(255) NULL,\n" +
                        "  PRIMARY KEY (`" + COL_ID + "`))";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SQL_COUNT_ITEMS = "SELECT COUNT(*) FROM " + TABLE_NAME;
    }

    /**
     * Returns all Experiences instances from the experience sql lite database.
     * @param context
     * @param isSent true if is_sent = 1, false if: is_sent = 0
     * @param isRead true if: is_read = 1 OR is_location_based = 0, false if: is_read = 0
     * @return List of experiences
     */
    public static ArrayList<ReceivedExperience> getAll(Context context, Boolean isSent, Boolean isRead) {
        ArrayList<ReceivedExperience> receivedExperiences = new ArrayList<>();
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ExperienceDbContract.TABLE_NAME + " WHERE 1=1 " +
                (isSent != null ? " AND " + ExperienceDbContract.COL_IS_SENT + " = " + (isSent ? 1 : 0) : "") +
                (isRead != null ? " AND (" + ExperienceDbContract.COL_IS_READ + " = " + (isRead ? "1 OR " + ExperienceDbContract.COL_IS_LOCATION_BASED + "=0)" : "0)") : "") +
                " ORDER BY " + ExperienceDbContract.COL_CREATED_AT + " DESC"
                , null);
        if(c.moveToFirst()){
            do{
                ReceivedExperience experience = new ReceivedExperience();
                experience = (ReceivedExperience) loadFromCursor(c, experience);
                receivedExperiences.add(experience);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return receivedExperiences;
    }

    /**
     * Returns the expected emotion as JSON string.
     * @return expected emotion (json string)
     */
    public String getExpectedEmotionJSON() {
        Gson gson = new Gson();
        String sExpectedEmotion = gson.toJson(expectedEmotion);
        return sExpectedEmotion;
    }

    /**
     * Returns the expected Emotion.
     * @return expected emotion
     */
    public Emotion getExpectedEmotion() {
        Emotion emotion = Emotion.getEmotionFromJson(this.emotion);
        return emotion;
    }

}
