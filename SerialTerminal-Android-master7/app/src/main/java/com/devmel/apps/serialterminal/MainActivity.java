package com.devmel.apps.serialterminal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devmel.communication.IUart;
import com.devmel.communication.android.UartBluetooth;
import com.devmel.communication.android.UartUsbOTG;
import com.devmel.communication.linkbus.Usart;
import com.devmel.storage.Node;
import com.devmel.storage.SimpleIPConfig;
import com.devmel.storage.android.UserPrefs;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.devmel.apps.serialterminal.R.id.graph1;



public class MainActivity extends AppCompatActivity {
    public final static String sharedPreferencesName = "com.devmel.apps.serialterminal";
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    public static ArrayList<Pair1> hickup = new ArrayList<Pair1>();
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
    SimpleDateFormat sdf1 = new SimpleDateFormat("s");
    private UserPrefs userPrefs;
    private int rxBytes;
    private int txBytes;
    private int err;
    private int numb;

    private Button connectBt;
    private Button userBt;
    private TextView statusText, textView_percentage;
    private EditText receiveText;
    private EditText sendText;
    private EditText transmitText;
    private CheckBox checkCR;
    private CheckBox checkLF;
    private EditText editText111;
    private ProgressBar progressbar;
    private ProgressBar pb1,pb2,pb3,pb4;

    private Thread thread;
    private IUart device;
    private String portName = null;
    private Class<?> portClass = null;
    private boolean init = false;
    private Handler handler = new Handler();
    public SeekBar seekBarKM;
    public GraphView graphView;
    private boolean lastCharge;
    private double lastBatStat;
    private int lastKM=0;
    private double countKM=0;
    List<Pair1> loc= new ArrayList<>();
    private int lastKmStat;
    private ArrayList<Double> medianEVP=new ArrayList<>();
    private String second;
    private int kmSinceLastRecharge=10;
    private int a;
    FloatingActionButton b1, b2, b3, b4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        series.setCustomPaint(paint);
        series.setBackgroundColor(Color.argb(60,255,255,255));
        series.setDrawBackground(true);
        graphView=(GraphView) findViewById(graph1);
        //GraphView graph = (GraphView) findViewById(R.id.graph);
        connectBt = (Button) findViewById(R.id.connectBt);
        userBt = (Button) findViewById(R.id.userBt);
        statusText = (TextView) findViewById(R.id.statusText);
        receiveText = (EditText) findViewById(R.id.receiveText);
        receiveText.setMovementMethod(new ScrollingMovementMethod());
        sendText = (EditText) findViewById(R.id.sendText);
        transmitText = (EditText) findViewById(R.id.transmitText);
        transmitText.setMovementMethod(new ScrollingMovementMethod());
        checkCR = (CheckBox) findViewById(R.id.checkCR);
        checkLF = (CheckBox) findViewById(R.id.checkLF);

        seekBarKM = (SeekBar) findViewById(R.id.seekBar);
        pb1 = (ProgressBar) findViewById(R.id.progressBar2);
        pb2 = (ProgressBar) findViewById(R.id.progressBar3);
        pb3 = (ProgressBar) findViewById(R.id.progressBar4);
        pb4 = (ProgressBar) findViewById(R.id.progressBar5);
        textView_percentage=(TextView) findViewById(R.id.textView_percentage);
        b1 = (FloatingActionButton) findViewById(R.id.floatingActionButton1);

        //Listener
        seekBarKM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!loc.equals(hickup.subList((i*hickup.size())/100,hickup.size()))) {
                    List kook = hickup.subList((i * hickup.size()) / 100, hickup.size());
                    resetGraph(kook);
                }
                loc = hickup.subList((i*hickup.size())/100,hickup.size());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            ;
            }
        });
        connectBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectClick();
            }
        });
        userBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userStats();
            }
        });

        pb2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                efficiencyStats();
            }
        });
        b1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                efficiencyStats();
            }
        });
//        b2.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                MapsStats();
//            }
//        });

        final Button optBt = (Button) findViewById(R.id.optBt);
        optBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                serialOptions();
            }
        });
        final Button clearBt = (Button) findViewById(R.id.clearBt);
        clearBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearClick();
                Save(hickup);


            }
        });


        final Button initBt = (Button) findViewById(R.id.initBt);
        initBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initAT();
            }
        });

        OBD_filter filterOBD = new OBD_filter();
        OBD_filter.PIDsArray();
        numb = 0;

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        initGraph();
        lastKmStat = 20000;
        lastBatStat = 50;
        a= 50;

    }

    @Override
    protected void onResume() {
        super.onResume();
        initPreferences();

        //Select Port
        String type = userPrefs.getString("selectedType");
        String name = userPrefs.getString("selectedName");

        boolean selected = false;
        if (type != null && name != null) {
            if (type.equals("LB")) {
                if (name.contains(" - ")) {
                    String[] names = name.split(" - ");
                    if (names != null && names.length > 0) {
                        Node devices = new Node(this.userPrefs, "Linkbus");
                        String[] ipDeviceList = devices.getChildNames();
                        if (ipDeviceList != null) {
                            for (String devStr : ipDeviceList) {
                                if (devStr.equals(names[0])) {
                                    portName = devStr;
                                    portClass = Usart.class;
                                    selected = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (type.equals("USB")) {
                String[] usbDeviceList = UartUsbOTG.list(getBaseContext());
                for (String devStr : usbDeviceList) {
                    if (devStr.equals(name)) {
                        portName = devStr;
                        portClass = UartUsbOTG.class;
                        selected = true;
                        break;
                    }
                }
            } else if (type.equals("BT")) {
                String[] btDeviceList = UartBluetooth.list();
                for (String devStr : btDeviceList) {
                    if (devStr.equals(name)) {
                        portName = devStr;
                        portClass = UartBluetooth.class;
                        selected = true;
                        break;
                    }
                }
            }
        }

        if (selected == false) {
            portName = "";
            portClass = null;
            statusText.setText(R.string.port_selected_none);
        } else {
            statusText.setText(getString(R.string.port_show) + portName);
        }

        if (userPrefs.getInt("configBaudrate") <= 0) {
            serialOptions();
        }

        if (userPrefs.getInt("CR") == 1) {
            checkCR.setChecked(true);
        } else {
            checkCR.setChecked(false);
        }

        if (userPrefs.getInt("LF") == 1) {
            checkLF.setChecked(true);
        } else {
            checkLF.setChecked(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            portSelect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPreferences() {
        if (userPrefs == null) {
            userPrefs = new UserPrefs(getSharedPreferences(MainActivity.sharedPreferencesName, Context.MODE_PRIVATE));
        }
    }


    private void lfClick() {
        if (checkLF.isChecked()) {
            userPrefs.saveInt("LF", 1);
        } else {
            userPrefs.saveInt("LF", 0);
        }
    }

    private void crClick() {
        if (checkCR.isChecked()) {
            userPrefs.saveInt("CR", 1);
        } else {
            userPrefs.saveInt("CR", 0);
        }

    }

    private void connectClick() {
        if (portClass == null) {
            portSelect();
        } else {
            if (device == null) {
                connect();
            } else {
                disconnect();
            }
        }
    }

    private void clearClick() {
        rxBytes = 0;
        txBytes = 0;
        err = 0;
        receiveText.setText("");
        transmitText.setText("");
    }

    private void sendChangingPIDs() {


        if (device != null && device.isOpen() == true && init) {
            //"atcra 374\r", "atcra 346\r", "atcra 412\r", "atcra 418\r", "atcra 231\r"
            final String[] PIDs = new String[]{"atcra 374\r", "atcra 346\r", "atcra 412\r", "atcra 418\r", "atcra 231\r"};
            final String msg = "\r atcra 374\r atma\r";

            //Runnable conRun1 = new Runnable() {
            //public void run() {
            try {
                if (numb >= PIDs.length) {
                    numb = 0;
                }
                String textInput = "\r";

                OutputStream out = device.getOutputStream();
                out.write(textInput.getBytes());
                out.flush();

                textInput = PIDs[numb];

                out = device.getOutputStream();
                out.write(textInput.getBytes());
                out.flush();

                textInput = "atma\r";

                out = device.getOutputStream();
                out.write(textInput.getBytes());
                out.flush();


                numb++;
                transmitText.setText(textInput);

            } catch (Exception e) {
                disconnect();
//						e.printStackTrace();
            }
            //}
            //};
            //new Thread(conRun1).start();
        }
    }

    private void initAT() {
        if (device != null && device.isOpen() == true) {
            final String[] initArr = new String[]{"atsp6\r", "ate0\r", "ath1\r", "atcaf0\r", "atcra 231\r", "atS0\r", "atma\r"};
            init = true;

            Runnable conRun = new Runnable() {
                public void run() {
                    try {
                        for (int i = 0; i < initArr.length; i++) {
                            String textInput = initArr[i];

                            OutputStream out = device.getOutputStream();
                            out.write(textInput.getBytes());
                            out.flush();
                            textSent(textInput);
                        }
                        handler.postDelayed(runnable, 100);
                    } catch (Exception e) {
                        disconnect();
//						e.printStackTrace();
                    }
                }
            };
            new Thread(conRun).start();
        }
    }

    private void sendClick() {
        if (device != null && device.isOpen() == true) {
            Runnable conRun = new Runnable() {
                public void run() {
                    try {
                        String textInput = sendText.getText().toString();
                        if (userPrefs.getInt("CR") == 1) {
                            textInput += "\r";
                        }
                        if (userPrefs.getInt("LF") == 1) {
                            textInput += "\n";
                        }
                        OutputStream out = device.getOutputStream();
                        out.write(textInput.getBytes());
                        out.flush();
                        textSent(textInput);
                    } catch (Exception e) {
                        disconnect();
//						e.printStackTrace();
                    }
                }
            };
            new Thread(conRun).start();
        }

    }

    private void textSent(final String textInput) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String textOriginal = transmitText.getText().toString();
                if (textOriginal.length() > 0) {
                    transmitText.setText(textOriginal + textInput);
                } else {
                    transmitText.setText(textInput);
                }
                txBytes += textInput.length();
                updateTrafficStatus();
            }
        });
    }

    private void textReceived(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //receiveText.setText(receiveText.getText().toString()+text);
                OBD_filter.packetReceived(text);

                CheckKm();
                CheckBatStatus(OBD_filter.getSOC());
                pb1.setProgress((int) OBD_filter.getEVP());
                pb2.setProgress((int) OBD_filter.getSOC()*150/100);
                pb3.setProgress((int) OBD_filter.getSOC());
                textView_percentage.setText(OBD_filter.getSOC() + "%");
                //textView_percentage.setText(Double.toString(OBD_filter.getSOC())+"%");


                if (!sdf1.format(new Date()).equals(second)) {
                    if(OBD_filter.getEVP()>0)
                    medianEVP.add(OBD_filter.getEVP());
                    second = sdf1.format(new Date());

                if (!(lastKM ==OBD_filter.getOdo())) {
                    countKM=countKM+1;
                    double avg=0;
                    for(int i=0;i<medianEVP.size();i++)
                        avg += medianEVP.get(i);
                    avg= avg/medianEVP.size();
                    DataPoint dary = new DataPoint(countKM, avg);
                    setGraph(dary);
                    medianEVP.clear();
                    lastKM=OBD_filter.getOdo();
                }}


            }
        });
    }

    private void updateTrafficStatus() {
        /*String text = "RX : "+this.rx Bytes+"  ;  TX : "+this.txBytes;
        if(err > 0){
            text += " ; ERR : "+err;
        }*/

        //statusText.setText(Double.toString(OBD_filter.EVP));
    }

    private void connectError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(message);
                connectBt.setText(R.string.connect);
            }
        });
        deviceUnselect();
    }

    private void notFound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(R.string.device_not_found);
                connectBt.setText(R.string.connect);
            }
        });
        deviceUnselect();
    }

    private void connect() {
        deviceUnselect();
        deviceSelect();

        if (device != null) {
            Runnable conRun = new Runnable() {
                public void run() {
                    try {
                        device.setParameters(userPrefs.getInt("configBaudrate"), (byte) userPrefs.getInt("configDatabits"), (byte) userPrefs.getInt("configStopbits"), (byte) userPrefs.getInt("configParity"));
                        device.open();
                    } catch (IOException e) {
                        connectError(e.getMessage());
                    } catch (Exception e) {
                    }
                }
            };
            execute(conRun);
            while (thread != null && thread.isAlive()) {
            }
            if (device == null) {
                return;
            }
            if (device.isOpen() == true) {
                //Open inStream
                try {
                    final InputStream inStream = device.getInputStream();
                    //Start read loop
                    Runnable r = new Runnable() {
                        public void run() {
                            try {
                                while (inStream != null && device.isOpen()) {
                                    try {
                                        int available = inStream.available();
                                        if (available > 0) {
                                            byte[] buffer = new byte[4096];
                                            int toRead = inStream.read(buffer, 0, available);
                                            if (toRead > 0) {
                                                textReceived(new String(buffer, 0, toRead));
                                                //thread.sleep(3000);
                                            }
                                        }
                                    } catch (IOException e) {
                                        err++;
                                    }
                                }
                            } catch (Exception e) {
//		    					e.printStackTrace();
                            }
                        }
                    };
                    execute(r);
                    rxBytes = 0;
                    txBytes = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectBt.setText(R.string.disconnect);
                            updateTrafficStatus();
                        }
                    });
                    //Set reset and vtg
                    if (device != null && device instanceof Usart) {
                        int reset = userPrefs.getInt("configReset");
                        int resetPulse = userPrefs.getInt("configResetPulse");
                        int vtg = userPrefs.getInt("configVtg");

                        try {
                            Usart dev = (Usart) device;
                            dev.setVTG((vtg == 1) ? true : false);
                            dev.setReset((reset == 1) ? true : false);
                            if (resetPulse > 0) {
                                dev.toggleReset(100);
                            }
                        } catch (Exception e) {
//                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    disconnect();
//					e.printStackTrace();
                }
            }
        }
        if (device == null || device.isOpen() == false) {
            notFound();
        }
    }

    private void disconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(R.string.disconnected);
                connectBt.setText(R.string.connect);
            }
        });
        deviceUnselect();
        init = false;
    }


    private void deviceSelect() {
        if (portClass != null) {
            if (portClass.equals(UartUsbOTG.class)) {
                try {
                    UartUsbOTG uart = new UartUsbOTG(portName, getBaseContext());
                    this.device = uart;
                } catch (Exception e) {
//					e.printStackTrace();
                }
            } else if (portClass.equals(UartBluetooth.class)) {
                try {
                    UartBluetooth uart = new UartBluetooth(portName);
                    this.device = uart;
                } catch (Exception e) {
//					e.printStackTrace();
                }
            } else if (portClass.equals(Usart.class)) {
                Node devices = new Node(this.userPrefs, "Linkbus");
                SimpleIPConfig device = SimpleIPConfig.createFromNode(devices, portName);
                if (device != null) {
                    Usart uart = new Usart(device);
                    //uart.setLock(userPrefs.getInt("lock")==1 ? true : false);
                    uart.setMode(Usart.MODE_ASYNCHRONOUS);
                    uart.setInterruptMode(true, 1000);
                    this.device = uart;
                }
            }
        }
    }

    private void deviceUnselect() {
        cancel();
        Runnable conRun = new Runnable() {
            public void run() {
                try {
                    device.close();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };
        execute(conRun);
        while (thread != null && thread.isAlive()) {
        }
        device = null;
    }
    public void efficiencyStats() {
        Intent intent = new Intent(this, EfficiencyStats.class);
        startActivity(intent);
        //eff.graphCalc(hickup);

    }


//    public void MapsStats(){
//        Intent intent = new Intent(this, TripPlannerActivity.class);
//        startActivity(intent);
//
//
//    }


    private void portSelect() {
        Intent intent = new Intent(this, PortSelect.class);
        startActivity(intent);
    }

    private void serialOptions() {
        Intent intent = new Intent(this, SerialOptions.class);
        startActivity(intent);
    }
    private void userStats() {
        Intent intent = new Intent(this, UserProfiling.class);
        startActivity(intent);
    }


    private void execute(Runnable r) {
        cancel();
        thread = new Thread(r);
        thread.start();
    }

    private void cancel() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void initGraph() {
        graphView = (GraphView) findViewById(graph1);    //new object
        graphView.setTitle("Energy Consumption");


        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Past Km");
        gridLabel.setVerticalAxisTitle("kW/km");
        gridLabel.setGridColor(Color.WHITE);
        gridLabel.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        gridLabel.setHorizontalAxisTitleColor(Color.WHITE);
        gridLabel.setHighlightZeroLines(false);
        gridLabel.setVerticalAxisTitleColor(Color.WHITE);
        graphView.setTitleColor(Color.WHITE);
        gridLabel.setHorizontalLabelsColor(Color.WHITE);
        gridLabel.setVerticalLabelsColor(Color.WHITE);
        hickup = Read();
        for (int i = 0; i < hickup.size(); i++)
            series.appendData(hickup.get(i).getDataPoint(), true, 200);
        graphView.addSeries(series);
        countKM=hickup.size();

    }

    public void resetGraph(List<Pair1> datda) {

        series.resetData(new DataPoint[]{});
        for (int j = 0; j < datda.size(); j++) {
            series.appendData(datda.get(j).getDataPoint(), true, 200);
        graphView.removeAllSeries();
        graphView.addSeries(series);
    }}

    public void setGraph(DataPoint input) {

        if (hickup.size() > 0) {
            DataPoint data = hickup.get(hickup.size() - 1).getDataPoint();
        }

        if (hickup.isEmpty() || hickup.get(hickup.size()-1).getDataPoint().getX() < input.getX()) {
            hickup.add(new Pair1(input, sdf.format(new Date())));
            //Save(hickup);
            series.appendData(input, true, 200);
            graphView.addSeries(series);
        }
    }

    public void Save(ArrayList<Pair1> theRing) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "ElecTrip.txt"));
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(theRing);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Pair1> Read() {

        FileInputStream fis = null;
        ArrayList<Pair1> theRing = new ArrayList<>();

        try {
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "ElecTrip.txt"));
            ObjectInputStream is = new ObjectInputStream(fis);
            theRing = (ArrayList<Pair1>) is.readObject();
            is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            File new_file = new File(Environment.getExternalStorageDirectory(), "ElecTrip.txt");
            try {
                new_file.createNewFile();
            } catch (IOException e1) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            File new_file = new File(Environment.getExternalStorageDirectory(), "ElecTrip.txt");
            new_file.delete();
        }
        return theRing;
    }

    private boolean LastRecharge(){
    if (OBD_filter.getQuickCharge() || OBD_filter.getOnBoardCharger() == true ){
        lastCharge = true; }
        else {
        lastCharge = false;}
        return lastCharge;

}

    public void CheckBatStatus(double batlvl) {

        if (batlvl != lastBatStat) {
            lastBatStat = batlvl;
            //Save((lastBatStat));
            //kmSinceLastRecharge=0;

        }
    }

    public int CheckKm(){
    if (OBD_filter.getOdo() != lastKmStat){
        kmSinceLastRecharge =  1 + lastKmStat;
        }
        return kmSinceLastRecharge;
    }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            sendChangingPIDs();      /* and here comes the "trick" */
            handler.postDelayed(this, 100);
        }
    };
}

class Pair1 implements Serializable {
    private static final long serialVersionUID = 6128016096756071380L;
    private String date;
    private DataPoint dataValue;

    public Pair1(DataPoint key, String value) {
        this.dataValue = key;
        this.date = value;
    }

    public DataPoint getDataPoint() {
        return this.dataValue;
    }

    public String getDate() {
        return this.date;
    }

}

