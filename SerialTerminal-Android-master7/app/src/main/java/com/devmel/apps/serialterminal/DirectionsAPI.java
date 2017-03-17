package com.example.sushi.mapsapplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sushi on 15-Nov-16.
 */

public class DirectionsAPI extends AsyncTask<String, Void,String[]> {

    private String result;
    private String paramsArray;
    int b;
    int amount;
    String[] data= new String[20];
    String[] data1;
    protected String[] doInBackground(String... params) {
        paramsArray = params[0];
        String responseString = null;
        String inputStream=null;
        String stringDump = new String();
        String routeDump = null;
        String infoDump = new String();
        String informationDump = new String();

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/xml?" + paramsArray+ "&alternatives=true&key=AIzaSyAwMrzd5bOY-iPQTW5YEw27SmbRt88I-qU");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                InputStream instream = (InputStream) conn.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                StringBuilder sb = new StringBuilder();

                try {
                    while ((inputStream = reader.readLine()) != null) {
                        sb.append(inputStream).append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader.close();
                instream.close();
                responseString = sb.toString();

                int i=7;
                int k=0;
                b=0;
                while(i >= 7) {
                    i = responseString.indexOf("<route>", i+1)+ "<route>".length();
                    k = responseString.indexOf("</route>", i+1);
                    routeDump=responseString.substring(i,k);
                    int a,a1;
                    a = routeDump.indexOf("<summary>")+ "<summary>".length();
                    a1 = routeDump.indexOf("</summary>");
                    int i1,k1;
                    i1 = routeDump.lastIndexOf("<duration>") ;
                    k1 = routeDump.lastIndexOf("</duration>");
                    int i2,k2;
                    i2 = routeDump.lastIndexOf("<distance>");
                    k2 = routeDump.lastIndexOf("</distance>");

                    informationDump=routeDump.substring(i1,k1)+routeDump.substring(i2,k2);
                    stringDump=routeDump.substring(a,a1);


                    if(i >= 7) {
                        int y=0;
                        for(int xo=0 ; xo<2 ; xo++) {

                            y = informationDump.indexOf("<text>", y+1)+ "<text>".length();
                            int y1 = informationDump.indexOf("</text>", y+1);
                            infoDump = infoDump +"<"+ informationDump.substring(y,y1)+">";

                        }
                        data[b] = String.valueOf(b+1) + ":" + "<" +stringDump+ ">" +infoDump+"#";
                        infoDump="";
                        informationDump="";
                        stringDump="";
                        for(int zz=0;zz<4;zz++) {

                            //routeDump.substring(routeDump.lastIndexOf("<lat>"),routeDump.lastIndexOf("</lat>"));
                           // routeDump.substring(routeDump.lastIndexOf("<lng>"),routeDump.lastIndexOf("</lng>"));
                            routeDump =routeDump.substring(0,routeDump.lastIndexOf("<lat>"));

                        }
                        int x=5;
                        while( x >= 5) {
                            x = routeDump.indexOf("<lat>", x+1)+ "<lat>".length();
                            int x1 = routeDump.indexOf("</lat>", x+1);
                            int x2 = routeDump.indexOf("<lng>", x+1)+ "<lng>".length();
                            int x3 = routeDump.indexOf("</lng>", x+1);
                            if( x >= 5) data[b] = data[b] +  routeDump.substring(x,x1) +","+ routeDump.substring(x2,x3)+"|";
                        }
                        data[b]=data[b].substring(0,data[b].length()-1);
                    }
                    b++;
                    amount++;

                }
                //  responseString = responseString.substring(start, end);

                //responseString= sb.toString();
            }

        } catch (IOException e) {
            //TODO Handle problems..
        }
        data1=data;
        //return str.substring(0,str.length()-1);
        //D//ouble[] joined22= new Double[b];
        //joined22= Arrays.copyOfRange(joined2, 0, b);
        return (data1);
    }





}
