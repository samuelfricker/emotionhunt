package ch.fhnw.ip5.emotionhunt.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Console;

import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.LocationHistory;

/**
 * EmotionHunt ch.fhnw.ip5.emotionhunt.helper
 *
 * @author Benjamin Bur
 */

public class DbHelper  extends SQLiteOpenHelper {
    /** Tag for logging */
    public static final String TAG = "DbHelper";
    /** Version of the database */
    public static final int DB_VERSION = 2;
    /** Name of the database */
    public static final String DB_NAME = "emotionhunt.db";

    /**
     * Constructor calling necessary methods.
     * @param context app context
     */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, "Creating database " + DB_NAME + " with version " + DB_VERSION);
        db.execSQL(Experience.ExperienceDbContract.SQL_CREATE_TABLE);
        db.execSQL(LocationHistory.LocationDbContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Experience.ExperienceDbContract.SQL_DROP_TABLE);
        db.execSQL(LocationHistory.LocationDbContract.SQL_DROP_TABLE);
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void getStatus(SQLiteDatabase db) {
        //count experiences
        Cursor mCount = db.rawQuery(Experience.ExperienceDbContract.SQL_COUNT_ITEMS, null);
        mCount.moveToFirst();
        Log.d(TAG, Experience.ExperienceDbContract.TABLE_NAME + ": " + mCount.getInt(0));
        mCount.close();

        //count locations
        mCount = db.rawQuery(LocationHistory.LocationDbContract.SQL_COUNT_ITEMS, null);
        mCount.moveToFirst();
        Log.d(TAG, LocationHistory.LocationDbContract.TABLE_NAME + ": " + mCount.getInt(0));
        mCount.close();
    }
}
