package com.example.rishugoel.weatherapi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rishugoel on 11/08/16.
 */
public class useIntent extends Service{

    final static String MY_ACTION = "MY_ACTION";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String data = intent.getExtras().getString("url");

        new Thread() {

            String weather = "UNDEFINED";
            public void run() {
                try {

                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(data).openConnection();

                    InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();

                    String inputString;
                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }

                    JSONObject topLevel = new JSONObject(builder.toString());
                    JSONObject main = topLevel.getJSONObject("main");
                    weather = String.valueOf(main.getDouble("temp"));

                    urlConnection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MY_ACTION);
                broadcastIntent.putExtra("weather","Current Weather in K:"+weather);
                sendBroadcast(broadcastIntent);

            }


        }.start();
        return 1;
    }
}
