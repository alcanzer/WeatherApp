package com.example.alcanzer.weatherapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


/*
api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}
*/


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.button)
    Button btn;
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.sunRise)
    TextView rise;
    @BindView(R.id.tempText)
    TextView temp;
    @BindView(R.id.main)
    TextView mainer;
    private final String SITE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private final String API_KEY = "&appid=ced5712cfe0799ed6b72d4aa36a11e11";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Typeface HomemadeApple = Typeface.createFromAsset(getAssets(), "fonts/HomemadeApple.ttf");
        mTextView.setTypeface(HomemadeApple);
        mTextView.setText("She sat in the rain waiting for a glimpse of a bright Sun.");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bundle bundle = data.getParcelableExtra("bundle");
            LatLng fromPosition = bundle.getParcelable("from_position");
            double lat = fromPosition.latitude;
            double lng = fromPosition.longitude;
            Toast.makeText(this, fromPosition.toString(), Toast.LENGTH_SHORT).show();

            GetWeather task = new GetWeather();

            try {
                task.execute(SITE_URL+"lat="+lat+"&lon="+lng+API_KEY).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }



    public class GetWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream inputStream = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();

                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String rext = "";
                JSONObject jsonObject = new JSONObject(s);
                JSONArray weather = jsonObject.getJSONArray("weather");
                for(int i = 0; i < weather.length(); i++){
                    JSONObject partweather = weather.getJSONObject(i);
                    rext += partweather.getString("main")+",";
                    mainer.setText(rext);
                    Toast.makeText(getApplicationContext(), partweather.getString("main"), Toast.LENGTH_LONG).show();
                }
                JSONObject mainObject = jsonObject.getJSONObject("main");
                Double currentTemp = Double.parseDouble(mainObject.getString("temp"));
                temp.setText(String.format("%.1f",(currentTemp - 273))+" °C");
                JSONObject jsonObject1 = jsonObject.getJSONObject("sys");
                if(jsonObject1.getString("country") != null){
                    mTextView.setText(jsonObject.getString("name"));
                }
                mTextView.setText(jsonObject.getString("name")+","+ jsonObject1.getString("country"));
                long sunrise = Long.parseLong(jsonObject1.getString("sunrise"));
                long sunset = Long.parseLong(jsonObject1.getString("sunset"));
                long daytime = sunset - sunrise;
                long hours =  (daytime/3600);
                long minutes = (daytime/60) % 60;

                if(minutes/10 != 0){

                    rise.setText("Total day hours"+"\n"+hours+":"+minutes);

                }else
                    rise.setText("Total day hours"+"\n"+hours+":0"+minutes);






            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
