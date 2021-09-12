package com.example.fieldmapview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.fieldmapview.Models.Album;
import com.example.fieldmapview.RecyclerView.AlbumsAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private GoogleApiClient client;
    private Marker currentLocationMarker;
    public final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude;
    double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
////////////////////////////////////////
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> albumList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///////////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
        ////////////////////////////////////////////////////
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        albumList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, albumList);
        recyclerView.setAdapter(adapter);
//        prepareAlbums();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{1,2,3,4,5,6,7,8,9,10
                };

        Album a = new Album("True Romance", 13, covers[0]);
        albumList.add(a);

        a = new Album("Xscpae", 8, covers[1]);
        albumList.add(a);

        a = new Album("Maroon 5", 11, covers[2]);
        albumList.add(a);

        a = new Album("Born to Die", 12, covers[3]);
        albumList.add(a);

        a = new Album("Honeymoon", 14, covers[4]);
        albumList.add(a);

        a = new Album("I Need a Doctor", 1, covers[5]);
        albumList.add(a);

        a = new Album("Loud", 11, covers[6]);
        albumList.add(a);

        a = new Album("Legend", 14, covers[7]);
        albumList.add(a);

        a = new Album("Hello", 11, covers[8]);
        albumList.add(a);

        a = new Album("Greatest Hits", 17, covers[9]);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng anyWhere = new LatLng(50,50);
        LatLng anyWhere1 = new LatLng(49,30);
        mMap.addMarker(new MarkerOptions().position(anyWhere).title("m2"));
        mMap.addMarker(new MarkerOptions().position(anyWhere1).title("m2"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        ////////////
        LatLng anyWhere = new LatLng(50, 50);
        Album a = new Album("True Romance", 13, 1);
            if (albumList.contains(a)){
                return;
            }
            else {
                if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(anyWhere) == true) { //kiểm tra chứa vị trị khi move camera
                    albumList.add(a);
                    adapter.notifyDataSetChanged();
                } else {
                    albumList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        //////////////////        ///////////////////
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this::onLocationChanged);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this::onLocationChanged);
        }
    }
    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
