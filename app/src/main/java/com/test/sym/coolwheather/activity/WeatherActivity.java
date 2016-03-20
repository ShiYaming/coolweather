package com.test.sym.coolwheather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.sym.coolwheather.R;
import com.test.sym.coolwheather.service.AutoUpdateService;
import com.test.sym.coolwheather.util.HttpCallbackListener;
import com.test.sym.coolwheather.util.HttpUtil;
import com.test.sym.coolwheather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener{

    private Button switchchCity;

    private Button refreshWeather;

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;  //show publish date time
    private TextView weatherDespText;  //describe weather info
    private TextView temp1Text;   //show temperature 1
    private TextView temp2Text;     //show temperature 2
    private TextView currentDateText;  //show current date time



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        //init
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("Sync...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            // local weather when no county code
            showWeather();
        }

        switchchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

    }

    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.switch_city:
                    Intent intent =new Intent(this,ChooseAreaActivity.class);
                    intent.putExtra("from_weather_activity", true);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.refresh_weather:
                    publishText.setText("Sync...");
                    SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
                    String weatherCode = prefs.getString("weather_code","");
                    if (!TextUtils.isEmpty(weatherCode)) {
                        queryWeatherInfo(weatherCode);
                    }
                    break;
                default:
                    break;
            }

    }

    /**
     *  query county weather
     */
    private void queryWeatherCode(String countycode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countycode + ".xml";
        queryFromServer(address,"countyCode");
    }

    /**
     * query weather info associated with weathercode
     * @param weatherCode
     */

    private void queryWeatherInfo(String weatherCode) {
        String address  = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(final  String address,final  String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("sync failed");
                    }
                });

            }
        });
    }
    /**
     * read info from SharedPreference file
     */
    private  void  showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天" + prefs.getString("publish_time","") + "发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        /**
         * autoUpdate start
         */
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
