package dev.pool2go;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Map extends AppCompatActivity implements OnMapReadyCallback, CallingActivity {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Thread server;
    static public String LOG_FILE_NAME = "pool2go_log.txt";

    /**
     * TODO: Fill in remaining Overridden methods
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
            logLocation(location);
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

        // TODO: Keep following logic to top-level activity
        fileFactory(LOG_FILE_NAME);

        // Start a dummy server on localhost
        try {
            server = new Thread(new TestServer(this));
            server.start();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Clean up our mess first before stopping
     */
    @Override
    public void onStop() {

        // it's possible the listener will be waiting for a last update after the app crashes
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);

        // Thread.stop() is deprecated, the server should instead check if it has been interrupted
        server.interrupt();

        super.onStop();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Start the location listener
        // TODO: How to make sure the device has an available location service? This can return null
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            // https://developer.android.com/guide/topics/location/strategies
            grabInitialLocation();

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
     * Check if file exists, if not, create it.
     *
     * @param filename
     */
    public void fileFactory(String filename) {
        if (!Arrays.asList(this.fileList()).contains(filename)) {
            // file does not exist, create it
            new File(this.getFilesDir(), filename);
            notifyUser("Created file " + filename);
        } else {
            notifyUser(filename + " already exists");
        }
    }

    /**
     * Grab the initial location, there's a bit of work that may be required:
     *
     * Unfortunately, if there is no last known location, we'll need to do extra work to grab a coarse
     * location. A fine location may be too slow for the user, and we want some sort of a UI update
     * ASAP.
     *
     * We need a new listener because the usual one will not set the proper zoom level.
     *
     * https://developer.android.com/reference/android/location/LocationManager#requestSingleUpdate(java.lang.String,%20android.app.PendingIntent)
     */
    public void grabInitialLocation() {
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null)
                setLocationAndZoom(location);
            else {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                setLocationAndZoom(location);
                                // TODO: this is usually only required if we request ongoing updates, remove?
                                locationManager.removeUpdates(this);
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
                        },
                        null);
            }
        } catch (SecurityException e) {
            e.getMessage();
        }
    }

    /**
     * Set new location, is usually called from locationListener. This update should not set a zoom
     * level.
     *
     * https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
     *
     * @param location passed from locationManager
     */
    public void setLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Set new location and zoom to a reasonable default.
     *
     * Using this to set initial update, as it should be fast enough for interacting on boot right away.
     *
     * @param location
     */
    public void setLocationAndZoom(Location location) {
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
        appendLog(LOG_FILE_NAME,
                location.getLatitude() + ", " + location.getLongitude(),
                false);
    }

    /**
     * Send the location to a server on localhost:8080.
     *
     * TODO: Re-write this when sending to a real server.
     *
     * @param location
     */
    public void sendLocation(Location location) {
        Socket socket;
        try {
            socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(location.getLatitude() + location.getLongitude());
            socket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Writes a string to the log file in the form of "$DATE, $STRING".
     *
     * @param s Contents to be written, date not required.
     * @param notify
     * @param filename
     */
    public void appendLog(String filename, String s, boolean notify) {
        // https://developer.android.com/reference/java/util/Date.html#toString()
        String date = java.util.Calendar.getInstance().getTime().toString();
        s = date + ", " + s;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            fileOutputStream.write(s.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (notify)
            notifyUser("Log appended");
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
