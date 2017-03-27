package fi.minscie.duckt.smartshovel;

import static fi.minscie.duckt.smartshovel.Constants.FIRST_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.SECOND_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.THIRD_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.FOURTH_COLUMN;
import static fi.minscie.duckt.smartshovel.Constants.FIFTH_COLUMN;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private InputStream is = null;
    private JSONArray jObj = null;
    private String json = "";
    private JSONArray readings;
    boolean done = false;

    private List<String> temp_list = new ArrayList<String>();
    private List<String> count_list = new ArrayList<String>();
    private List<String> weight_list = new ArrayList<String>();
    private List<String> time_list = new ArrayList<String>();
    private List<String> timestamp_list = new ArrayList<String>();


    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Thread thread = new Thread() {

                            public void run() {

                                Log.d("Debug", "1");
                                HttpURLConnection urlConnection = null;
                                Log.d("Debug", "2");
                                String url = "https://iot-backend-metropolia.herokuapp.com/api/data/10";
                                String result = null;
                                Log.d("Debug", "3");

                                // Making HTTP request
                                try {
                                    Log.d("1", "1. Connecting...");
                                    urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
                                    Log.d("2", "2. Connecting...");
                                    urlConnection.setUseCaches(false);
                                    Log.d("3", "3. Connecting...");
                                    urlConnection.setRequestProperty("Content-Type", "application/json");
                                    Log.d("4", "4. Connecting...");
                                    urlConnection.setRequestMethod("GET");
                                    Log.d("5", "5. Connecting...");
                                    urlConnection.connect();
                                    Log.d("6", "Connected");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                BufferedReader bufferedReader = null;
                                try {
                                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String line = null;
                                StringBuilder sb = new StringBuilder();

                                try {
                                    while ((line = bufferedReader.readLine()) != null) {
                                        sb.append(line);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                result = sb.toString();

                                json = result.toString();
                                Log.d("Got JSON: ", json);


                                // try parse the string to a JSON object

                                try {
                                    jObj = new JSONArray(json);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                Log.d("Got JSON: ", String.valueOf(jObj));
                                for (int i = 0; i < jObj.length(); i++) {


                                    try {
                                        Log.d("Parsing "+i+":", String.valueOf(jObj.getJSONObject(i)));
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        Log.d("Parsing timestamp", (jObj.getJSONObject(i).getString("timeStamp")));
                                        timestamp_list.add(jObj.getJSONObject(i).getString("timeStamp"));
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        readings = jObj.getJSONObject(i).getJSONArray("readings");
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                            for (int j = 0; j < readings.length(); j++) {

                                                try {
                                                    Log.d("Parsing time", (readings.getJSONObject(j).getString("time")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    time_list.add(readings.getJSONObject(j).getString("time"));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    Log.d("Parsing weight", (readings.getJSONObject(j).getString("weight")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    weight_list.add(readings.getJSONObject(j).getString("weight"));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    Log.d("Parsing count", (readings.getJSONObject(j).getString("count")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    count_list.add(readings.getJSONObject(j).getString("count"));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    Log.d("Parsing temp", (readings.getJSONObject(j).getString("temp")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                try {
                                                    temp_list.add(readings.getJSONObject(j).getString("temp"));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                                Log.d("time in time_array is ", String.valueOf(time_list));

                                            }


                                }


                            }

        };
        thread.start();
        try {
            thread.join();



            ListView historyListview = (ListView) findViewById(R.id.historyListView);
            list = new ArrayList<HashMap<String, String>>();

            Log.d("2.", "Test");
            int i = 0;


            for (i = 0; i < timestamp_list.size(); i++) {
                HashMap<String, String> values = new HashMap<String, String>();


                values.put(FIRST_COLUMN, timestamp_list.get(i).substring(0,10));
                values.put(SECOND_COLUMN, time_list.get(i));
                values.put(THIRD_COLUMN, weight_list.get(i).substring(0,5));
                values.put(FOURTH_COLUMN, temp_list.get(i).substring(0,2));
                values.put(FIFTH_COLUMN, count_list.get(i));
                list.add(values);
            }

            HistoryListAdapter adapter = new HistoryListAdapter(this, list);
            historyListview.setAdapter(adapter);

            Log.d("3.", "Test");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}


