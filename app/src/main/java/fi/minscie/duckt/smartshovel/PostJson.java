package fi.minscie.duckt.smartshovel;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DuckT on 20/03/2017.
 */

public class PostJson implements Runnable{
    private String json;
    public PostJson(String json) {
        this.json = json;
    }@Override
    public void run() {
        Log.d("Debug", "1");
        HttpURLConnection urlConnection;
        Log.d("Debug", "2");
        String url = "https://iot-backend-metropolia.herokuapp.com/api/data/10";
        String data = json;
        String result = null;
        Log.d("Debug", "3");
        try {
            Log.d("1","1. Connecting...");
            urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
            Log.d("2","2. Connecting...");
            urlConnection.setDoOutput(true);
            Log.d("3","3. Connecting...");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            Log.d("4","4. Connecting...");
            urlConnection.setRequestMethod("POST");
            Log.d("5","5. Connecting...");
            urlConnection.connect();
            Log.d("6","Connected");

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(data);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Result","Result: "+result);
        if(result.equals("Data Saved")){
        }
    }
}
