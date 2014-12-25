package com.willinger.shmulik.androidlocation;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    TextView lm_Network_latitude;
    TextView lm_Network_longitude;
    TextView lm_GPS_latitude;
    TextView lm_GPS_longitude;
    TextView fused_latitude;
    TextView fused_longitude;

    Button btnShowLocation;
    LocationService appLocationService;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The LocationListener Service - for the LocationManager API
        appLocationService = new LocationService(MainActivity.this);

        // The GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        initTextViews();
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                GetFusedLocation();
                GetLocationManagerLocationWithGPS();
                GetLocationManagerLocationWithNetwork();
            }
        });
    }

    private void initTextViews()
    {
        lm_Network_latitude = (TextView) findViewById(R.id.LM_Network_latitude);
        lm_Network_longitude = (TextView) findViewById(R.id.LM_Network_longitude);

        lm_GPS_latitude = (TextView) findViewById(R.id.LM_GPS_latitude);
        lm_GPS_longitude = (TextView) findViewById(R.id.LM_GPS_longitude);

        fused_latitude = (TextView) findViewById(R.id.Fused_latitude);
        fused_longitude = (TextView) findViewById(R.id.Fused_longitude);
    }

    public void openLocationSettings(View v)
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent);
    }

    private void GetLocationManagerLocationWithNetwork()
    {
        Location networkLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null)
        {
            lm_Network_latitude.setText(Double.toString (networkLocation.getLatitude()));
            lm_Network_longitude.setText(Double.toString (networkLocation.getLongitude()));
        }
        else
        {
            lm_Network_latitude.setText("Network is not enabled");
            lm_Network_longitude.setText("");
        }
    }

    private void GetLocationManagerLocationWithGPS()
    {
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null)
        {
            lm_GPS_latitude.setText(Double.toString (gpsLocation.getLatitude()));
            lm_GPS_longitude.setText(Double.toString (gpsLocation.getLongitude()));
        }
        else
        {
            lm_GPS_latitude.setText("GPS is not enabled");
            lm_GPS_longitude.setText("");
        }
    }

    private void GetFusedLocation()
    {
        Location fusedLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (fusedLocation != null)
        {
            fused_latitude.setText(Double.toString (fusedLocation.getLatitude()));
            fused_longitude.setText(Double.toString (fusedLocation.getLongitude()));
        }
        else
        {
            fused_latitude.setText("Location is not enabled");
            fused_longitude.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

class LocationService extends Service implements LocationListener
{
    protected LocationManager locationManager;

    public LocationService(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation(String provider)
    {
        if (locationManager.isProviderEnabled(provider))
        {
            locationManager.requestLocationUpdates(provider,0, 0, this);
            if (locationManager != null)
                return locationManager.getLastKnownLocation(provider);
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public IBinder onBind(Intent arg0) { return null;  }

}
