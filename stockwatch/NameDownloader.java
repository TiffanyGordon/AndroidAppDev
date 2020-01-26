package com.ariana.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NameDownloader extends android.os.AsyncTask<String, Void, String> {

    private static final String TAG = "NameDownloader";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static HashMap<String, String> data = new HashMap<>();

    public static final String baseInfoURL = "https://api.iextrading.com/1.0/ref-data/symbols";

    NameDownloader(MainActivity main) {
        mainActivity = main;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.updateData(data);
    }

    @Override
    //Establishes a connection to the database containing symbol and stock names, and initiates parsing of that info
    protected String doInBackground(String... params) {

        Uri dataUri = Uri.parse(baseInfoURL);
        String baseURL = dataUri.toString();
        Log.d(TAG, "doInBackground: " + baseURL);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(baseURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());
            conn.setRequestMethod("GET");
            InputStream input = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(input)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        parseJSON(sb.toString());

        return null;
    }

    //Puts JSON data into a hashmap of stock name/symbol pairs
    private void parseJSON(String s) {


        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");
                data.put(symbol, name);
            }

            Log.d(TAG, "onPostExecute: " + data);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

