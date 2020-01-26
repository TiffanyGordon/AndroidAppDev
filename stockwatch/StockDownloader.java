package com.ariana.stockwatch;


import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class StockDownloader extends android.os.AsyncTask<String, Void, Stocks> {

    private static final String TAG = "StockDownloader";
    private MainActivity mainActivity;
    public Stocks stock;

    public static final String rtDataURL = "https://cloud.iexapis.com/stable/stock/";
    public static final String APIKey = "pk_d0047aecd2be492cb753f30cc5c05ab1";

    StockDownloader(MainActivity main) {
        mainActivity = main;
    }

    @Override
    protected void onPostExecute(Stocks stock) {
        mainActivity.updateStocks(stock);
    }

    @Override
    //Opens a connection to the database and initiates the data parse
    protected Stocks doInBackground(String... mArr) {

       for (int j = 0; j < mArr.length; j++) {
           String m = mArr[j];

           Uri.Builder buildURL = Uri.parse(rtDataURL).buildUpon();
           buildURL.appendEncodedPath(m);
           buildURL.appendEncodedPath("/quote?token=" + APIKey);
           String urlToUse = buildURL.build().toString();

           Log.d(TAG, "doInBackground: " + urlToUse);

           StringBuilder sb = new StringBuilder();
           try {
               URL url = new URL(urlToUse);

               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setRequestMethod("GET");
               InputStream input = conn.getInputStream();
               BufferedReader reader = new BufferedReader((new InputStreamReader(input)));

               String line;
               while ((line = reader.readLine()) != null) {
                   sb.append(line).append('\n');
               }

               Log.d(TAG, "doInBackground: " + sb.toString());
               parseJSON(sb.toString());

           } catch (Exception e) {
               Log.e(TAG, "doInBackground: ", e);
               return null;
           }

       }

        return stock;
    }

    //Retrieves the stock data and puts it into a stock object
    private Stocks parseJSON(String s) {

        try {
            JSONObject sData = new JSONObject(s);

            String symbol = sData.getString("symbol");
            String name = sData.getString("companyName");
            String value = sData.getString("latestPrice");
            String change = sData.getString("change");
            String percent = sData.getString("changePercent");

            Log.d(TAG, symbol + "; " + name + "; " + value + "; " + change + "; " + percent);

            stock = new Stocks(symbol, name, Double.valueOf(value), Double.valueOf(change), Double.valueOf(percent));
            return stock;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
