package com.example.sushi.mapsapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String[] directionsDemo;
    String startPoint;
    String snappedRoad;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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


        //the format should be ("Lat,Long|Lat,Long|.........|Lat,Long") -> 1 parameter per position point!
        //ElevationAPI().execute(directionsDemo[0]).get();
        //the format should be ("origin=copenhagen&destination=herlev")
        try {

            directionsDemo = new DirectionsAPI().execute("origin=copenhagen&destination=norrebro").get();

            //a little logistick, choose 1st road alternative, split the stringso you get only lat long, not road name, distance or time
            directionsDemo[0] = directionsDemo[0].substring(directionsDemo[0].indexOf("#")+ "#".length());

            //makes sure the lines follow the road
            snappedRoad = new RoadsAPI().execute(directionsDemo[0]).get();

            //finally, show on the map
            mMap.addPolyline(new mapsAPI().onmapsAPIReady(directionsDemo[0]));


            //a little logistick, get the first pair of lat/long coordinates and focus the map on it
            startPoint = snappedRoad.substring(0,snappedRoad.indexOf("|"));
            String[] valuesParts = startPoint.split(",",2);
            LatLng sydney = new LatLng(Double.parseDouble(valuesParts[0]),Double.parseDouble(valuesParts[1]));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));
            } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        }

    }

