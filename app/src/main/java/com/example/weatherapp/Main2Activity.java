package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;


public class Main2Activity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    public void getWeather(View view)
    {
        DouwnloadWeather douwnloadWeather = new DouwnloadWeather();
        douwnloadWeather.execute("https://openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.textView2);
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
                Toast.makeText(Main2Activity.this, "Couldn't find weather", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Main2Activity.this, "error Couldn't find weather\nPlease make sure you typed the name correctly", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
