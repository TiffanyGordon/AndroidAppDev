package com.ariana.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Stocks> stocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter sAdapter;
    private SwipeRefreshLayout swiper;

    private HashMap<String, String> data = new HashMap<>();
    private ArrayList<String> matchArray = new ArrayList<>();
    private ArrayList<String> multMatchArray = new ArrayList<>();
    private ArrayList<Stocks> added = new ArrayList<>();

    private static final String TAG = "DATA";
    private static final String CHECK = "DELETE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        sAdapter = new StockAdapter(stocksList, this);

        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swipeRefresh);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        //retrieve user's stocks from JSON file
        loadFile();

        //check network connection
        connectionCheck();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Refreshes values for each stock in list
    private void doRefresh() {
                added.clear();
                added = stocksList;
                for (int i = 0; i < stocksList.size(); i++) {
                    Stocks curr = added.get(i);
                    String currSym = curr.getSymbol();
                    Log.d(TAG, "Updating: " + currSym);
                    getStockValues(currSym);
                }

                stocksList.clear();
                swiper.setRefreshing(false);
    }

    //Retrieves and reads file of saved stocks.  Stores the details for each stock in a JSON object.
    public void loadFile() {

        stocksList.clear();
        try {
            InputStream inputStream = openFileInput("Stocks.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();

                String jsonText = stringBuilder.toString();

                try {
                    JSONArray jsonArray = new JSONArray(jsonText);
                    Log.d(TAG, "loadFile: " + jsonArray.length());

                    for (int i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String symbol = jsonObject.getString("symbol");
                        String name = jsonObject.getString("name");
                        Double value = jsonObject.getDouble("value");
                        Double change = jsonObject.getDouble("change");
                        Double percent = jsonObject.getDouble("percent");
                        Stocks stock = new Stocks(symbol, name, value, change, percent);
                        stocksList.add(stock);
                    }

                    Log.d(TAG, "loadFile: " + stocksList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadFile: File not found: \" + e.toString()");
        } catch (IOException e) {
            Log.d(TAG, "loadFile: Can not read file: " + e.toString());
        }
    }

    //Verifies an internet connection has been established and alerts the user if not.
    private void connectionCheck() {
        ConnectivityManager connMan =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            //Execute NameDownloader async task
            new NameDownloader(this).execute();
        } else {
            //show no internet dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                        }

                    });

            builder.setMessage("Stocks cannot be updated until an internet connection is established.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void updateData(HashMap<String, String> fromND) {

        data.putAll(fromND);
        Log.d(TAG, "HashMap: " + data);
    }

    //Updates the list of stocks when a new symbol is added.
    public void updateStocks(Stocks stock) {
        Boolean matched = false;
        String newSym = stock.getSymbol();
        Log.d(TAG, "Comparing for add: " + newSym);
        if (stocksList.size() != 0) {
            for (int i = 0; i < stocksList.size(); i++) {
                Stocks curr = stocksList.get(i);
                String currSym = curr.getSymbol();
                Log.d(TAG, currSym);

                if (newSym.equals(currSym)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                }

                            });

                    builder.setMessage("This stock symbol is already in your list");
                    builder.setTitle("Duplicate Stock");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    matched = true;
                }
            }
            if (matched == false) {
                stocksList.add(stock);
                Collections.sort(stocksList, Stocks.orderStocks);
                fileSave();
                sAdapter.notifyDataSetChanged();
            }
        } else {
            stocksList.add(stock);
            fileSave();
            sAdapter.notifyDataSetChanged();
        }
    }

    //Saves current list of stocks and their details to file
    protected void fileSave() {
        try {
            Log.d(TAG, "fileSave: Saving JSON File");

            FileOutputStream fos = openFileOutput("Stocks.txt", Context.MODE_PRIVATE);
            JSONArray jsonArray = new JSONArray();

            for (Stocks stock : stocksList) {
                try {
                    JSONObject stockJSON = new JSONObject();
                    stockJSON.put("symbol", stock.getSymbol());
                    stockJSON.put("name", stock.getName());
                    stockJSON.put("value", stock.getValue());
                    stockJSON.put("change", stock.getChange());
                    stockJSON.put("percent", stock.getPercent());

                    jsonArray.put(stockJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String jsonText = jsonArray.toString();
            Log.d(TAG, "saveStocks: JSON: " + jsonText);
            fos.write(jsonText.getBytes());
            fos.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error on Save", Toast.LENGTH_SHORT).show();

        }
    }

    //Opens MarketWatch link to stock details upon short click in recycler
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Stocks stock = stocksList.get(pos);
        String symbol = stock.getSymbol();
        String url = "https://www.marketwatch.com/investing/stock/" + symbol;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    //Upon long click in recycler, provides user with option to delete the stock from list
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Stocks stock = stocksList.get(pos);
        Log.d(CHECK, "Stock: " + stock);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Delete stock symbol %s?", stock.getSymbol()));
        builder.setTitle("Delete Stock");
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        stocksList.remove(pos);
                        sAdapter.notifyDataSetChanged();
                        fileSave();
                    }
                });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ConnectivityManager connMan =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return true;
        }

        NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            //Execute NameDownloader async task
            addStock();
        } else {
            //show no internet dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                        }

                    });

            builder.setMessage("Stocks cannot be updated until an internet connection is established.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    //Allows user to enter a stock symbol or part of a stock name to add to the list
    public boolean addStock() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                matchArray.clear();
                CharSequence req = input.getText().toString();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (entry.getKey().contains(req) | entry.getValue().contains(req)) {
                        matchArray.add(entry.getKey());
                    }
                }
                Log.d(TAG, "onClick: " + matchArray);
                if (matchArray.size() == 1) {
                    getStockValues(matchArray.get(0));
                } else if (matchArray.size() > 1) {
                    multMatchArray.clear();
                    CharSequence[] matches = matchArray.toArray(new CharSequence[matchArray.size()]);
                    for (int i = 0; i < matchArray.size(); i++) {
                        String item = matches[i].toString();
                        String elementVal = data.get(item);
                        multMatchArray.add(matches[i] + " - " + elementVal);
                    }
                    Collections.sort(multMatchArray);
                    Collections.sort(matchArray);
                    listDialog();
                } else {
                    //symbol not found dialog
                    symNotFound();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setMessage("Please enter a stock symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private void getStockValues(String m) {
        new StockDownloader(this).execute(m);
    }

    //Allows user to make a selection when multiple stocks match the input term
    private void listDialog() {
        final CharSequence[] matches = multMatchArray.toArray(new CharSequence[multMatchArray.size()]);
        Log.d(TAG, "Matches: " + matches);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        builder.setItems(matches, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getStockValues(matchArray.get(which));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog2 = builder.create();
        dialog2.show();
    }

    public void symNotFound() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);

        builder3.setMessage("The symbol you requested was not found in our database.");
        builder3.setTitle("Symbol Not Found");

        AlertDialog dialog3 = builder3.create();
        dialog3.show();
    }

}
