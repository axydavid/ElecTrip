package com.example.sushi.mapsapplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sushi on 01-Nov-16.
 */

public class ElevationAPI extends AsyncTask<String, Void,Double[]> {

    private String result;
    private String paramsArray;
    ArrayList<String>joined= new ArrayList<String>();
    Double[] joined2= new Double[500];
    int b;
    protected Double[] doInBackground(String... params) {
        paramsArray = params[0];
        String responseString = null;
        String inputStream=null;
        String[] stringDump = new String[500];
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/elevation/xml?path=" + paramsArray+ "&samples=400&key=AIzaSyAwMrzd5bOY-iPQTW5YEw27SmbRt88I-qU");
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
                int i=1;
                int k=0;
                b=0;
                while(i >= 0) {
                    i = responseString.indexOf("<elevation>", i+1)+ "<elevation>".length();
                    k = responseString.indexOf("</elevation>", i+1);
                    stringDump[b]=responseString.substring(i,k);
                    try
                    {
                        Double.parseDouble(String.valueOf(responseString.substring(i,k)));
                    }
                    catch(NumberFormatException e)
                    {
                        break;
                    }
                    joined2[b]=Double.parseDouble(responseString.substring(i,k));
                    b++;
                }
                  //  responseString = responseString.substring(start, end);

                //responseString= sb.toString();
            }

        } catch (IOException e) {
            //TODO Handle problems..
        }
        Double[] joined22= new Double[b];
        joined22=Arrays.copyOfRange(joined2, 0, b);
        return joined22;
    }



}
