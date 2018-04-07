package com.sanketkumbhare.weatherinfo.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sanketkumbhare.weatherinfo.R;
import com.sanketkumbhare.weatherinfo.model.Weather;
import com.sanketkumbhare.weatherinfo.tasks.ParseResult;
import com.sanketkumbhare.weatherinfo.utils.UnitConvertor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;

public class GraphActivity extends AppCompatActivity {

    SharedPreferences sp;

   private ArrayList<Weather> mWeatherList = new ArrayList<>();

    float min = 100000;
    float max = 0;

    float minRain = 100000;
    float maxRain = 0;

    float minPressure = 100000;
    float maxPressure = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        setsupportToolbar();
        checkResult();


    }


    public void setsupportToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.graph_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
}



    public void checkResult(){
        sp = PreferenceManager.getDefaultSharedPreferences(GraphActivity.this);
        String lastLongterm = sp.getString("lastLongterm", "");

        if (parseLongTermJson(lastLongterm) == ParseResult.OK) {
            temperatureGraph();
            rainGraph();
            pressureGraph();

        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_err_parsing_json, Snackbar.LENGTH_LONG).show();
        }
    }



    private void temperatureGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph_temperature);

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < mWeatherList.size(); i++) {
            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(mWeatherList.get(i).getTemperature()), sp);

            if (temperature < min) {
                min = temperature;
            }

            if (temperature > max) {
                max = temperature;
            }

            dataset.addPoint(getDateLabel(mWeatherList.get(i), i), (float) temperature);
        }
        dataset.setSmooth(false);
        dataset.setColor(Color.parseColor("#FF5722"));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#333333"));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues((int) (Math.round(min)) - 1, (int) (Math.round(max)) + 1);
        lineChartView.setStep(2);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);

        lineChartView.show();
    }

    private void rainGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph_rain);

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < mWeatherList.size(); i++) {
            float rain = Float.parseFloat(mWeatherList.get(i).getRain());

            if (rain < minRain) {
                minRain = rain;
            }

            if (rain > maxRain) {
                maxRain = rain;
            }

            dataset.addPoint(getDateLabel(mWeatherList.get(i), i), rain);
        }
        dataset.setSmooth(false);
        dataset.setColor(Color.parseColor("#2196F3"));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#333333"));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues(0, (int) (Math.round(maxRain)) + 1);
        lineChartView.setStep(1);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);

        lineChartView.show();
    }

    private void pressureGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph_pressure);

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < mWeatherList.size(); i++) {
            float pressure = UnitConvertor.convertPressure(Float.parseFloat(mWeatherList.get(i).getPressure()), sp);

            if (pressure < minPressure) {
                minPressure = pressure;
            }

            if (pressure > maxPressure) {
                maxPressure = pressure;
            }

            dataset.addPoint(getDateLabel(mWeatherList.get(i), i), pressure);
        }
        dataset.setSmooth(true);
        dataset.setColor(Color.parseColor("#4CAF50"));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#333333"));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues((int) minPressure - 1, (int) maxPressure + 1);
        lineChartView.setStep(2);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);

        lineChartView.show();
    }


    public ParseResult parseLongTermJson(String result) {
        int i;
        try {
            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                return ParseResult.CITY_NOT_FOUND;
            }

            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
                Weather weather = new Weather();

                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem.getJSONObject("main");

                JSONObject windObj = listItem.optJSONObject("wind");
                weather.setWind(windObj.getString("speed"));

                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));

                JSONObject rainObj = listItem.optJSONObject("rain");
                JSONObject snowObj = listItem.optJSONObject("snow");
                if (rainObj != null) {
                    weather.setRain(MainActivity.getRainString(rainObj));
                } else {
                    weather.setRain(MainActivity.getRainString(snowObj));
                }

                weather.setDate(listItem.getString("dt"));
                weather.setTemperature(main.getString("temp"));

                mWeatherList.add(weather);
            }
        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            return ParseResult.JSON_EXCEPTION;
        }

        return ParseResult.OK;
    }

    String previous = "";

    public String getDateLabel(Weather weather, int i) {
        if ((i + 4) % 4 == 0) {
            SimpleDateFormat resultFormat = new SimpleDateFormat("E");
            resultFormat.setTimeZone(TimeZone.getDefault());
            String output = resultFormat.format(weather.getDate());
            if (!output.equals(previous)) {
                previous = output;
                return output;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

}
