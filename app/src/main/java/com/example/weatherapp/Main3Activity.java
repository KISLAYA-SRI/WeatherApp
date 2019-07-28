package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main3Activity extends AppCompatActivity implements LocationListener{

    LocationManager locationManager;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please turn on the location", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 ,0, (LocationListener) this);

    }

    double lat=0, lon=0;
    @Override
    public void onLocationChanged(Location location) {
        textView = (TextView)findViewById(R.id.textView);
        lat = location.getLatitude();
        lon = location.getLongitude();
        DouwnloadWeather douwnloadWeather = new DouwnloadWeather();
        douwnloadWeather.execute("https://openweathermap.org/data/2.5/weather?lat="+location.getLatitude()+"&lon="+location.getLongitude()+"&appid=b6907d289e10d714a6e88b30761fae22");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    class DouwnloadWeather extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while(data != -1)
                {
                    char current = (char)data;
                    result = result + current;
                    data = inputStreamReader.read();
                }
                return result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(Main3Activity.this, "Couldn't find weather", Toast.LENGTH_LONG).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray arr = new JSONArray(jsonObject.getString("weather"));
                String Message = "";
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject obj = arr.getJSONObject(i);
                    String main = obj.getString("main");
                    String description = obj.getString("description");

                    if(!main.equals("") && !description.equals(""))
                    {
                        Message = Message + "\nMain : "+main;
                        Message = Message + "\nDescription : "+description;
                    }
                }
                JSONObject object = jsonObject.getJSONObject("main");

                String temp = object.getString("temp");
                String pressure = object.getString("humidity");
                String humidity = object.getString("pressure");

                if(!temp.equals("") && !humidity.equals("") && !pressure.equals(""))
                {
                    Message = Message + "\nTemperature : "+temp +" °C";
                    Message = Message + "\nPressure : "+pressure;
                    Message = Message + "\nHumidity : "+humidity;
                }

                if(!Message.equals("")) {
                    textView.setText(Message);
                }
            } catch (JSONException e) {
                Toast.makeText(Main3Activity.this, "error Couldn't find weather\nPlease make sure you typed the name correctly", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}


