package ch.fhnw.ip5.emotionhunt.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ch.fhnw.ip5.emotionhunt.R;
import ch.fhnw.ip5.emotionhunt.helpers.DbHelper;
import ch.fhnw.ip5.emotionhunt.helpers.PermissionHelper;
import ch.fhnw.ip5.emotionhunt.models.Experience;
import ch.fhnw.ip5.emotionhunt.models.ReceivedExperience;
import ch.fhnw.ip5.emotionhunt.services.LocationService;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ArrayList<ReceivedExperience> mExperiences;
    private ArrayList<Marker> mMarkers;
    private Thread mExperienceListenerThread;
    private FrameLayout layoutCounter;
    private TextView txtCounter;
    private FloatingActionButton fabToggle;
    public static final int ONBAORDING_CODE = 1;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    boolean isCameraMoved = false;
    boolean isPublic = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase db = new DbHelper(getApplicationContext()).getWritableDatabase();
        DbHelper.getStatus(db);
        db.close();

        //check permission for location listener and require its permissions if necessary
        if (!PermissionHelper.checkLocationPermission(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        layoutCounter = (FrameLayout) findViewById(R.id.layout_counter);
        txtCounter = (TextView) findViewById(R.id.txt_counter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo_medium_main);

        fabToggle = (FloatingActionButton) findViewById(R.id.btn_toggle_public_private);
        fabToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePublicPrivate();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_main_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExperienceCreateActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");
        //setup map styles
        try {
            // Customise the styling of the base map using a JSON object defined in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        startExperienceListener();

        if (PermissionHelper.checkLocationPermission(this)) {
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult");
        switch (requestCode) {
            case PermissionHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start api service
                    if (PermissionHelper.checkLocationPermission(this)) {
                        startService(new Intent(MainActivity.this, LocationService.class));
                        mMap.setMyLocationEnabled(true);
                        buildGoogleApiClient();
                        mGoogleApiClient.connect();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG,"buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            switch (item.getItemId()) {
                case R.id.btn_main_settings:
                    Intent intentDetail = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intentDetail);
                    return true;
                case R.id.btn_main_experience_list:
                    Intent intentList = new Intent(getApplicationContext(), ExperienceListActivity.class);
                    startActivity(intentList);
                    return true;
                case R.id.btn_main_profile:
                    Intent intentProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                    intentProfile.putExtra(ProfileActivity.EXTRA_IS_OWN_USER,true);
                    startActivity(intentProfile);
                    return true;
                default:
                    throw new IllegalArgumentException("Invalid Action Menu Item");
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not found appropriate Menu Action. Error Message: "+e.getMessage());
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged");
        //place marker at current position
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        try {
            LocationService.insertLocation(location, getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //move camera when location has speed (>2kmh)
        boolean hasSpeed = location.hasSpeed() && location.getSpeed() > 0.55f;

        //zoom to current position:
        if (!isCameraMoved || hasSpeed) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).build();
            if (isCameraMoved) {
                cameraPosition = new CameraPosition.Builder().target(latLng).zoom(mMap.getCameraPosition().zoom).build();
            }
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            isCameraMoved = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"onConnected");
        if (PermissionHelper.checkLocationPermission(this)) {
            Log.d(TAG,"onConnected permissions granted");
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(1000); //1 second
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(0.5F); //1/2 meter

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed");
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();

        //obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Update the marker's icon and returns whether there was an update possible or not.
     * @param experience
     * @return
     */
    public boolean updateMarkerIcon (Experience experience) {
        int markerIndex = mExperiences.indexOf(experience);

        //assert that there is a matching experience found
        if (markerIndex == -1) return false;
        Marker marker = mMarkers.get(markerIndex);

        //assert that there is a matching marker found
        if (marker == null) return false;
        marker.setIcon(experience.getMarkerIcon(getApplicationContext()));
        return true;
    }

    /**
     * Adds a marker to the array list and on the google maps screen if not yet added.
     * @param experience new experience
     * @return successfully added
     */
    public boolean addExperience(ReceivedExperience experience) {
        //do not show not-location based experiences on map
        if (!experience.isLocationBased) {
            return false;
        }

        //prevent adding sent experiences
        if (experience.isSent && experience.isPrivate()) return false;

        if (mExperiences == null) mExperiences = new ArrayList<>();

        //return if marker was updated
        if (updateMarkerIcon(experience)) {
            return false;
        }

        //prevent adding duplicates
        if (mExperiences.contains(experience)) {
            //already added
            return false;
        }

        if (mMarkers == null) mMarkers = new ArrayList<>();

        LatLng marker = new LatLng(experience.lat, experience.lon);
        MarkerOptions options = new MarkerOptions().position(marker).title(experience.text);
        options.icon(experience.getMarkerIcon(getApplicationContext()));
        Marker m = mMap.addMarker(options);
        m.setTitle(String.valueOf(experience.id));

        mExperiences.add(experience);
        mMarkers.add(m);
        Log.d(TAG, "Experience " + experience.id + " successfully added on map.");
        return true;
    }

    /**
     * Starts the experience listener and update or initializes the marker.
     */
    public void startExperienceListener() {
        layoutCounter.setVisibility(View.INVISIBLE);
        mExperiences = new ArrayList<>();
        mMarkers = new ArrayList<>();
        mExperienceListenerThread = new Thread(new Runnable() {
            public void run() {
                while (true && !mExperienceListenerThread.isInterrupted()) {
                    if (mMap == null) {
                        Log.d(TAG, Thread.currentThread().getId() + ": Map is not ready for listener...");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) { }
                        continue;
                    }

                    ArrayList<ReceivedExperience> receivedExperiences = Experience.getAll(getApplicationContext(), null, null);
                    int countUnreadPublic = 0;
                    int countUnreadPrivate = 0;
                    for (final ReceivedExperience receivedExperience : receivedExperiences) {
                        if (!receivedExperience.isSent && !receivedExperience.isRead && receivedExperience.isLocationBased) {
                            if (receivedExperience.isPublic) {
                                countUnreadPublic++;
                            } else {
                                countUnreadPrivate++;
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (receivedExperience.isPublic == isPublic) {
                                    addExperience(receivedExperience);
                                }
                            }
                        });
                    }
                    if ((countUnreadPublic == 0 && isPublic) || (countUnreadPrivate == 0 && !isPublic)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutCounter.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        final int finalCount = isPublic ? countUnreadPublic : countUnreadPrivate;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtCounter.setText(String.valueOf(finalCount));
                                layoutCounter.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        mExperienceListenerThread.start();
    }

    /**
     * Stops the experience listener thread.
     */
    public void stopExperienceListener() {
        mExperienceListenerThread.interrupt();
        //remove all markers
        mMap.clear();
    }

    /**
     * Toggles the markers by the visibility of the experience (public / private).
     */
    private void togglePublicPrivate() {
        //stop listener
        stopExperienceListener();
        //invert public-private flag
        this.isPublic = !this.isPublic;
        //start listener again
        startExperienceListener();

        if (isPublic) {
            fabToggle.setImageResource(R.drawable.ic_public_white_24dp);
            Toast.makeText(this, R.string.show_public_experiences, Toast.LENGTH_SHORT).show();
        } else {
            fabToggle.setImageResource(R.drawable.ic_private_white_24dp);
            Toast.makeText(this, R.string.show_private_experiences, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker m) {
        int index = Integer.valueOf(m.getTitle());
        Experience experience = ReceivedExperience.findById(this,index);

        Log.d(TAG, "Experience '" + experience.id + "' clicked.");
        if (experience.isRead || experience.isCatchable(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), ExperienceDetailActivity.class);
            intent.putExtra(ExperienceDetailActivity.EXTRA_EXPERIENCE_ID, experience.id);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.not_catchable, Toast.LENGTH_LONG).show();
        }

        return true;
    }
}
