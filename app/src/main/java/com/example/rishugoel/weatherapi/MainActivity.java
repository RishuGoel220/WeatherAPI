package com.example.rishugoel.weatherapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.net.Uri;
import android.content.*;

import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;


public class MainActivity extends AppCompatActivity {
    private static final String APP_ID = "e5254c8011fcdd41b54535ee5e44d7ef";

    myreceiver MyReceiver;

    EditText editText;
    Button useAsync;
    Button useIntentService;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void findAllViewsById() {
        editText = (EditText) findViewById(R.id.editText);
        useAsync = (Button) findViewById(R.id.button2);
        useIntentService = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
    }

    protected  void onStart (){
        super.onStart();
        MyReceiver = new myreceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(useIntent.MY_ACTION);
        registerReceiver(MyReceiver, intentFilter);



        this.findAllViewsById();
        useIntentService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = String.format("http://api.openweathermap.org/data/2.5/weather?q="+editText.getText()+"&appid=%s",
                        APP_ID);
                Intent useintent = new Intent(getBaseContext(),useIntent.class);
                useintent.putExtra("url", url);
                startService(useintent);
            }
        });
        useAsync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = String.format("http://api.openweathermap.org/data/2.5/weather?q="+editText.getText()+"&appid=%s",
                       APP_ID);

                new openWeather(textView).execute(url);
            }
        });
    }


    public class myreceiver extends BroadcastReceiver{
        final static String MY_ACTION = "MY_ACTION";
        @Override
        public void onReceive(Context context, Intent intent){
            textView.setText(intent.getExtras().getString("weather"));
        }



    }


    private class openWeather extends AsyncTask<String, Void, String> {
        private TextView textView;

        public openWeather(TextView textView) {
            this.textView = textView;
        }

        @Override
        protected String doInBackground(String... strings) {
            String weather = "UNDEFINED";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
            return weather;
        }

        @Override
        protected void onPostExecute(String temp) {
            textView.setText("Current Weather in K : " + temp);
        }
    }
}
