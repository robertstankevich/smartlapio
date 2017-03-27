package fi.minscie.duckt.smartshovel;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Main2Activity extends AppCompatActivity {


    private TextView textView;
    private TextView averTemp;
    private TextView countTextView;
    private TextView lastWeightTextView;
    private TextView totalWeightTextView;
    private TextView averageWeightTextView;
    private TextView clockTextView;


    String jsonStr;
    private static final String TAG = "Main2Activity";
    int top_temp = 0;
    float average_temp = 0;
    float total_temp = 0;
    int count = 0;
    int status = 0;
    int old_status = 0;
    float weight = 0;
    float total_weight = 0;
    float average_weight = 0;
    String clock;
    int hh = 0;
    int min = 0;
    int sec = 0;
    int ms = 0;



    ArrayList<String> temp_array = new ArrayList<>();
    ArrayList<String> count_array = new ArrayList<>();
    ArrayList<String> weight_array = new ArrayList<>();
    ArrayList<String> time_array = new ArrayList<>();
    Button startButton;
    Button restartButton;
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
        top_temp = BluetoothConnectionService.getTop_Temp();
        }
        catch (NumberFormatException e){
            Log.e(TAG, "Invalid temp value, skipped!");
        }
            status = BluetoothConnectionService.getStatus();
        try {
            weight = BluetoothConnectionService.getWeight();
        }
        catch (NumberFormatException e){
            Log.e(TAG, "Invalid weight value, skipped!");
        }

            textView = (TextView) findViewById(R.id.temperatureValueTextView);
        averTemp = (TextView) findViewById(R.id.averageTemperatureTextView);
        countTextView = (TextView) findViewById(R.id.countValueTextView);
        lastWeightTextView = (TextView) findViewById(R.id.lastWeightValueTextView);
        totalWeightTextView = (TextView) findViewById(R.id.weightValueTextView);
        averageWeightTextView = (TextView) findViewById(R.id.averageWeightValueTextView);
        clockTextView = (TextView) findViewById(R.id.textView2);

        textView.setText(Integer.toString(top_temp));

    }
    public void historyButtonClicked(View v) throws JSONException{
        Intent activityintent = new Intent(Main2Activity.this, HistoryActivity.class);
        startActivity(activityintent);
    }

    public void startButtonClicked(View v) throws JSONException {
        t.start();

    }

    public void restartButtonClicked(View v) throws JSONException {
        restart();
    }

    public void stopButtonClicked(View v) throws JSONException {
        try{

            temp_array.add(Float.toString(average_temp));
            weight_array.add(Float.toString(total_weight));
            count_array.add(Integer.toString(count));
            time_array.add(clock);
            Log.d(TAG, "Array timestamp:" + time_array);
            Log.d(TAG, "Array av temp: "+ temp_array);
            Log.d(TAG, "Array total weight: " + weight_array);
            Log.d(TAG, "Array count: " + count_array);



            JSONObject workObj = new JSONObject();

            JSONArray workArray = new JSONArray();


            try {
                for (int i = 0; i < count_array.size(); i++) {
                    JSONObject WorkValues = new JSONObject();

                    WorkValues.put("time", time_array.get(i));
                    workArray.put(i, WorkValues);
                    WorkValues.put("temp", temp_array.get(i));
                    workArray.put(i, WorkValues);
                    WorkValues.put("weight", weight_array.get(i));
                    workArray.put(i, WorkValues);
                    WorkValues.put("count", count_array.get(i));
                    workArray.put(i, WorkValues);


                }
                workObj.put("name", "Work");
                workObj.put("description", "Finished Jobs");
                workObj.put("sensorStatus", "1");
                workObj.put("readings", workArray);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonStr = workObj.toString();
            Log.d("json: ", jsonStr);

            Runnable postJson = new PostJson(jsonStr);
            new Thread(postJson).start();

        } catch (Exception e){
            Log.e(TAG,"Error: "+e);
        }

        restart();
        if (startButton != null)
            startButton.setClickable(true);

    }









    Thread t = new Thread() {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(250);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setContentView(R.layout.activity_main2);
                            try {
                                status = BluetoothConnectionService.getStatus();
                            } catch (Exception e){
                                Log.e(TAG, "Invalid status value");
                            }
                                if(status != old_status && status == 1) {
                                count++;
                                try {
                                    top_temp = BluetoothConnectionService.getTop_Temp();
                                    total_temp += top_temp;
                                    average_temp = total_temp / count;
                                }
                                catch (Exception e){
                                    Log.e(TAG, "Invalid temp value, skipped!");
                                }
                                    try {
                                    weight = BluetoothConnectionService.getWeight();
                                    total_weight += weight;
                                    average_weight = (float) total_weight / count;
                                }
                                catch (Exception e){
                                    Log.e(TAG, "Invalid weight value, skipped!");
                                }



                                old_status = status;



                            }
                            else if(status != old_status && status == 0){
                                old_status = status;
                            }

                            ms++;

                            if (ms == 4) {
                                ms = 0;
                                sec++;
                            }
                            if (sec == 60) {
                                sec = 0;
                                min++;
                            }
                            if (min == 60) {
                                min = 0;
                                hh++;
                            }

                            clock = hh + ":" + min + ":" + sec;


                            textView = (TextView) findViewById(R.id.temperatureValueTextView);
                            averTemp = (TextView) findViewById(R.id.averageTemperatureTextView);
                            countTextView = (TextView) findViewById(R.id.countValueTextView);
                            lastWeightTextView = (TextView) findViewById(R.id.lastWeightValueTextView);
                            totalWeightTextView = (TextView) findViewById(R.id.weightValueTextView);
                            averageWeightTextView = (TextView) findViewById(R.id.averageWeightValueTextView);
                            clockTextView = (TextView) findViewById(R.id.textView2);



                            textView.setText(Integer.toString(top_temp));
                            averTemp.setText(String.format("%.1f", average_temp));
                            countTextView.setText(Integer.toString(count));
                            lastWeightTextView.setText(String.format("%.1f", weight));
                            totalWeightTextView.setText(String.format("%.1f", total_weight));
                            averageWeightTextView.setText(String.format("%.1f", average_weight));
                            clockTextView.setText(clock);

                        }
                    });
                }
            } catch (InterruptedException e) {
                Log.e(TAG,"Error: "+e);
            }



        }
    };



    private void restart(){
        temp_array.clear();
        temp_array.add(Integer.toString(top_temp));
        top_temp = 0;
        count = 0;
        status = 0;
        old_status = 0;
        ms=0;
        sec=0;
        min=0;
        hh=0;
        average_temp=0;
        total_temp = 0;
        average_weight=0;
        weight=0;
        total_weight=0;
    }
}



