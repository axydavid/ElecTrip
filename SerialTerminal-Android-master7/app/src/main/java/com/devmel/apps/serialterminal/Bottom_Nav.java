package com.devmel.apps.serialterminal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iv on 01.11.2016 г..
 */

public class Bottom_Nav extends Fragment {
    FloatingActionButton b1, b2, b3, b4;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_nav_fragment,
                container, false);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                Bottom_Nav.this.startActivity(myIntent);
            }
        });


//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent myIntent = new Intent(getActivity(), MapsActivity.class);
//                Bottom_Nav.this.startActivity(myIntent);
//
//            }
//        });


        return view;
    }
}
