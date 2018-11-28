package com.example.android.app_hci;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView ShowText;
    private ProgressBar pb;
    private int progressBarValue;
    private ImageView iv;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private boolean LocationService = false;
    public boolean mLocationPermissionGranted = true;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private boolean gps_enabled, network_enabled;
    private static final String TAG = "MainActivity";
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    public double latitude, longitude;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    //Callback for Location events.
    private LocationCallback mLocationCallback;

    private GoogleApiClient mGoogleApiClient;

    //Setting a listener for bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    Intent intent = new Intent(MainActivity.this.getApplication(), MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                case R.id.navigation_viewStory:
//                    startActivity(new Intent(this, ViewStoryActivity.class));
                    Intent intent2 = new Intent(MainActivity.this.getApplication(), ViewStoryActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
//                case R.id.navigation_storylist:
//                    mTextMessage.setText(R.string.title_storylist);
//                    return true;
                case R.id.navigation_stars:
                    Intent intent4 = new Intent(MainActivity.this.getApplication(), AchievementActivity.class);
                    startActivity(intent4);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        ShowText = (TextView)findViewById(R.id.text_progressbar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        progressBarValue = 30;
        pb.setProgress(progressBarValue);
        ShowText.setText(progressBarValue +"/"+pb.getMax());

        iv = findViewById(R.id.imageView1);
        iv.setOnClickListener(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Check for google services to load google map
        isServicesWorking();

        //Check if location turned on
        isLocationOn();
        //Check if app has permission for location
        getLocationPermission();

        if (this.mGoogleApiClient == null) {
            this.mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }
        this.mLocationRequest = LocationRequest.create().setPriority(100).setInterval(30000).setFastestInterval(3000);
    }


    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.imageView1:
                //Start walking activity when image is selected
                this.startActivity(new Intent(this, WalkingActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    protected void onResume() {
        super.onResume();
        this.mGoogleApiClient.connect();
    }

    protected void onPause() {
        super.onPause();
        Log.v(getClass().getSimpleName(), "onPause()");
        if (this.mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, (LocationListener) this);
            this.mGoogleApiClient.disconnect();
        }
    }

    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public void onConnectionSuspended(int i) {
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                return;
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                return;
            }
        }
        Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
    }

    public void onLocationChanged(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
    public void isLocationOn()
    {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        while(LocationService == false)
            try
            {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                LocationService = true;
            }catch (Exception ex){}
        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            LocationService = true;
        }catch (Exception ex){}
        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.please_turn_on_gps));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MainActivity.this.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    public boolean isServicesWorking()
    {
        Log.d(TAG, "isServicesWorking: Checking goggle services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            //Everything is fine
            Log.d(TAG, "isServicesWorking: Google Play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //An error occured but we can resolved it
            Log.d(TAG, "isServicesWorking: An error occured but we can resolve it)");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            //Unable to load Google Map
            Toast.makeText(this, "Can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void getLocationPermission()
    {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


}
