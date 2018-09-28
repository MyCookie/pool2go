package dev.pool2go;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static public String LOG_FILE_NAME = "pool2go_log.txt";

    /**
     * TODO: Fill in remaining Overridden methods
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
            logLocation(location);
            //notifyUser("Location updated");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // the tag is the id within the activity lifecycle, ie. like "map" in this activity
        //new DisplayFileList().show(getFragmentManager(), "fileListDialog");

        // TODO: Move logfile logic to top-level activity
        File logFile;
        if (!Arrays.asList(this.fileList()).contains(LOG_FILE_NAME)) {
            // logfile does not exist, create it
            logFile = new File(this.getFilesDir(), LOG_FILE_NAME);
            notifyUser("Created logfile " + LOG_FILE_NAME);
        } else {
            notifyUser("Logfile exists " + LOG_FILE_NAME);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Draw the blue dot
        mMap.setMyLocationEnabled(true);

        // Start the location listener
        // TODO: How to make sure the device has an available location service? This can return null
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            //TODO: Better design to grab location from the listener, or explicitly grab it first?
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setLocation(location);

            // Request a location update every 5 seconds or 10 meters, whichever happens first
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    locationListener);
        } catch (SecurityException e) {
            // No suitable permission present
            e.getMessage();
        }
    }

    /**
     * Set new location, is usually called from locationListener.
     *
     * https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
     *
     * @param location passed from locationManager
     */
    public void setLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Move camera and zoom in to level 15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    /**
     * Log the new location in the logfile.
     *
     * @param location passed from locationManager
     */
    public void logLocation(Location location) {
        // https://developer.android.com/reference/java/util/Date.html#toString()
        String date = java.util.Calendar.getInstance().getTime().toString();
        String fileContents = date + ", " + location.getLatitude() + ", " + location.getLongitude();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            fileOutputStream.write(fileContents.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * We can't traditionally make a Snackbar with only a Fragment, but each Fragment inherits an
     * invisible View. So grab the implicit view and use it to make a Snackbar.
     *
     * @param message
     */
    public void notifyUser(String message) {
        Snackbar.make(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getView(),
                message,
                Snackbar.LENGTH_LONG).show();
    }
}