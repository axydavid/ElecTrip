package com.devmel.apps.serialterminal;
import android.content.Intent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created by iv on 2.12.2016 г..
 */

public class StatisticsBars   extends Fragment {
    public ProgressBar pb1,pb2,pb3;

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle
    savedInstanceState) {

        View view =  inflater.inflate(R.layout.progressbar_fragment,
                container, false);
        pb1 = (ProgressBar) view.findViewById(R.id.progressBar4);
        //pb1.setProgress((int) OBD_filter.getSOC());
        pb2 = (ProgressBar) view.findViewById(R.id.progressBar3);
        pb3 = (ProgressBar) view.findViewById(R.id.progressBar4);

        pb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent myIntent = new Intent(getActivity(), DistanceStatActivity.class);

               // StatisticsBars.this.startActivity(myIntent);
            }
        });

        pb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent myIntent = new Intent(getActivity(), EnergyStatActivity.class);

               // StatisticsBars.this.startActivity(myIntent);
            }
        });

        /**pb3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent myIntent = new Intent(getActivity(), DistanceStatActivity.class);

        StatisticsBars.this.startActivity(myIntent);
        }
        });**/


        return view;

    }



}
