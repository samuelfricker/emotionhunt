package ch.fhnw.ip5.emotionhunt.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.services.LocationService;

import static org.junit.Assert.*;

/**
 * Android Test class for LocationHistory
 */
@RunWith(AndroidJUnit4.class)
public class LocationHistoryTest {
    Context context = InstrumentationRegistry.getTargetContext();
    LocationHistory locationHistory;

    private SQLiteDatabase getDb() {
        return new DbHelper(context).getWritableDatabase();
    }

    private LocationHistory dummyLocationHistory(int index) {
        LocationHistory lh = new LocationHistory();
        lh.lat = 0.0;
        lh.lon = 0.0;
        lh.provider = "test";
        lh.accuracy = 1.0f;
        lh.createdAt = index;
        return lh;
    }

    @Before
    public void setUp() throws Exception {
        locationHistory = new LocationHistory();
        LocationService.cleanUpAllEntries(getDb());
    }

    @After
    public void tearDown() throws Exception {
        LocationService.cleanUpAllEntries(getDb());
    }

    @Test
    public void countTest0() throws Exception {
        assertEquals(0, LocationHistory.count(getDb()));
    }

    @Test
    public void countTest1() throws Exception {
        LocationHistory lh = dummyLocationHistory(1);
        lh.saveDb(context);
        assertEquals(1, LocationHistory.count(getDb()));
    }

    @Test
    public void getLastPositionHistory() throws Exception {
        LocationHistory lh1 = dummyLocationHistory(2);
        lh1.provider = "provider-1";
        assertTrue(lh1.saveDb(context));

        LocationHistory lh2 = dummyLocationHistory(3);
        lh2.provider = "provider-1";
        lh2.lat = 47.00;
        assertTrue(lh2.saveDb(context));

        LocationHistory lh3 = dummyLocationHistory(4);
        lh3.provider = "provider-2";
        lh3.lat = 13.00;
        assertTrue(lh3.saveDb(context));

        //assert test provider last position
        LocationHistory lastPositionHistory1 = LocationHistory.getLastPositionHistory(context,"provider-1");
        assertNotNull(lastPositionHistory1);
        assertEquals(lastPositionHistory1.lat, lh2.lat, 0.001);

        //assert test2 provider last position
        LocationHistory lastPositionHistory2 = LocationHistory.getLastPositionHistory(context,"provider-2");
        assertNotNull(lastPositionHistory2);
        assertEquals(lastPositionHistory2.lat, lh3.lat, 0.001);
    }

    @Test
    public void saveDb() throws Exception {
        float accuracy = 0.5f;
        String provider = "test";
        double lat = 1.0;
        double lon = -24.154;

        locationHistory.accuracy = accuracy;
        locationHistory.provider = provider;
        locationHistory.lat = lat;
        locationHistory.lon = lon;
        assertTrue(locationHistory.saveDb(context));

        LocationHistory lastPositionHistory = LocationHistory.getLastPositionHistory(context, provider);
        assertNotNull(lastPositionHistory);
        assertEquals(lastPositionHistory.provider,provider);
        assertEquals(lastPositionHistory.accuracy,accuracy,0.001);
        assertEquals(lastPositionHistory.lat,lat,0.001);
        assertEquals(lastPositionHistory.lon,lon,0.001);
    }

}