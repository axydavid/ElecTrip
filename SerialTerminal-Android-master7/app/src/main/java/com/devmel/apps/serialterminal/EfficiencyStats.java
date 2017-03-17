package com.devmel.apps.serialterminal;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;

import static com.devmel.apps.serialterminal.MainActivity.hickup;

public class EfficiencyStats extends Activity {
    private EditText editText111;
    public TextView textView_avg, textView_avg1, textView_avg2, textView_avg3, textView_avg4;
    public ImageView imageView1, imageView2, imageView3, imageView4;
    BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
    public GraphView graphView2;
    String lastValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficiency_stat);
        GraphView graph = (GraphView) findViewById(R.id.graphBar);
        //graphBar =(GraphView) findViewById(graphView2);
        initBarGraph();
        initAvgTxt();
        graphCalc();
        //editText111= (EditText) findViewById(R.id.editText111);
        imageView1 = (ImageView)findViewById(R.id.imageView2);
        imageView2 = (ImageView)findViewById(R.id.imageView3);
        imageView3 = (ImageView)findViewById(R.id.imageView4);
        imageView4 = (ImageView)findViewById(R.id.imageView5);
    }

    public void initBarGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graphBar);
        graph.setTitle("Energy Consumption");
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();

        gridLabel.setHorizontalAxisTitle("Day");
        gridLabel.setVerticalAxisTitle("kW/km");
        gridLabel.setGridColor(Color.TRANSPARENT);

        gridLabel.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        gridLabel.setHorizontalAxisTitleColor(Color.WHITE);

        gridLabel.setVerticalAxisTitleColor(Color.WHITE);
        graph.setTitleColor(Color.WHITE);
        gridLabel.setHorizontalLabelsColor(Color.WHITE);
        gridLabel.setVerticalLabelsColor(Color.WHITE);

    }

    public void initAvgTxt(){
        textView_avg = (TextView) findViewById(R.id.textView_avg_label);
        textView_avg1 = (TextView) findViewById(R.id.textView_avg1);
        textView_avg2 = (TextView) findViewById(R.id.textView_avg2);
        textView_avg3 = (TextView) findViewById(R.id.textView_avg3);
        textView_avg4 = (TextView) findViewById(R.id.textView_avg4);

//        textView_avg1.setText("Today" + Double.toString(OBD_filter.getSOC()));
//        textView_avg2.setText("Today" + Double.toString(OBD_filter.getSOC()));
//        textView_avg3.setText("Today" + Double.toString(OBD_filter.getSOC()));
//        textView_avg4.setText("Today" + Double.toString(OBD_filter.getSOC()));
    }


    public void graphCalc() {
        ArrayList<Pair1> davidsParam = hickup;
        ArrayList<Double> intArray = new ArrayList<>();
        int b=0;
        GraphView graph = (GraphView) findViewById(R.id.graphBar);
        BarGraphSeries<DataPoint> serieso=new BarGraphSeries<>();
        serieso.setSpacing(5);
        //serieso.appendData();
        Paint pinta = new Paint();
        pinta.setColor(Color.WHITE);
        pinta.setAntiAlias(true);
        serieso.setCustomPaint(pinta);

        for (int i = 1; i < davidsParam.size(); i++) {
            intArray.add(intArray.size(), davidsParam.get(i-1).getDataPoint().getY());
            if (i==davidsParam.size()-1 || !davidsParam.get(i-1).getDate().equals(davidsParam.get(i).getDate())) {
                 serieso.appendData(new DataPoint(b,getMean(intArray)),true,100);
                serieso.setDrawValuesOnTop(true);
                serieso.setValuesOnTopColor(Color.WHITE);
                graph.removeAllSeries();
                graph.addSeries(serieso);
                intArray.clear();
                intArray.add(0,davidsParam.get(i-1).getDataPoint().getY());
                b++;
            }
        }
         serieso.appendData(new DataPoint(b,0),true,100);
        graph.addSeries(serieso);
    }

    public double getMean(ArrayList<Double> numberList) {
        double total = 0;
        for (int i = 0; i < numberList.size(); i++) {
            total += numberList.get(i);
        }
        total = total / (numberList.size());
        return total;
    }


}

