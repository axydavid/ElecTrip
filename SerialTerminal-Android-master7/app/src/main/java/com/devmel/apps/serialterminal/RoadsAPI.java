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
 * Created by Sushi on 01-Nov-16.
 */

public class RoadsAPI extends AsyncTask<String, Void,String> {

    private String paramsArray;
    int b;
    protected String doInBackground(String... params) {
        paramsArray = params[0];
        String responseString = null;
        String inputStream=null;
        String stringDump = "";
        try {
            URL url = new URL("https://roads.googleapis.com/v1/snapToRoads?path=" + paramsArray+ "&interpolate=true&key=AIzaSyAwMrzd5bOY-iPQTW5YEw27SmbRt88I-qU");
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
                int i=11,i1=0,k=0,k1=0, i2=0;
                b=0;
                String lat,lon;
                while(i >0) {
                    i = responseString.indexOf("\"latitude\": ", i+1)+ "\"latitude\": ".length();
                    if (i2>i)break;
                    k = responseString.indexOf("\n", i+1);
                    i1 = responseString.indexOf("\"longitude\": ", i+1)+ "\"longitude\": ".length();
                    k1 = responseString.indexOf("\n", i1+1);
                    lat = responseString.substring(i,k);
                    lon =responseString.substring(i1,k1);
                    stringDump=stringDump + lat + lon +"|";
                    i2=i;
                    b++;
                }
                stringDump=stringDump.substring(0,stringDump.length()-1);
            }

        } catch (IOException e) {
            //TODO Handle problems..
        }

        return stringDump;
    }



}
