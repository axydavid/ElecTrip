package com.example.sushi.mapsapplication;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
/**
 * Created by Sushi on 24-Nov-16.
 */

public class mapsAPI {
    String[] values;
    String[]  valuesParts= new String[4];
    public PolylineOptions onmapsAPIReady(String in){
    PolylineOptions polylineOptions = new PolylineOptions();
        values = in.split("\\|");

        for(int i=0;i<values.length;i++){
            valuesParts = values[i].split(",",2);
            polylineOptions.add(new LatLng(Double.parseDouble(valuesParts[0]),Double.parseDouble(valuesParts[1])));
        }

        return polylineOptions;
}
}