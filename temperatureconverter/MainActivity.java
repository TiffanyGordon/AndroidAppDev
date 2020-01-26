package com.ariana.temperatureconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView history;
    private static String convType;
    private EditText fahrenheit;
    private EditText celsius;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        convType = "F";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);

        history = findViewById(R.id.display);
        fahrenheit = findViewById(R.id.finput);
        celsius = findViewById(R.id.cinput);
    }

    //radioClicked reads the radio button selection
    public void radioClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        preferences.edit().putBoolean("CLICKED",((RadioButton) v).isChecked()).apply();

        switch(v.getId()) {
            case R.id.ftoc:
                if (checked)
                    convType = "F";
                    break;
            case R.id.ctof:
                if (checked)
                    convType = "C";
                    break;
        }

        Log.d(TAG, "Input Type: " + convType);
    }

    //convert takes the user inputted value and converts it with either from fahrenheit to celsius or from celsius to fahrenheit based on which radio button has been selected.
    public void convert(View v) {

        fahrenheit = findViewById(R.id.finput);
        celsius = findViewById(R.id.cinput);
        history = findViewById(R.id.display);
        history.setMovementMethod(new ScrollingMovementMethod());

        String fString = fahrenheit.getText().toString();
        String cString = celsius.getText().toString();

        if (convType == "F") {
            if (fString.equals("")) Toast.makeText(this, "No value entered.", Toast.LENGTH_LONG).show();
            else {
                double fValue = Double.parseDouble(fString);
                Log.d(TAG, "F: " + fValue);
                double converted = (fValue - 32.0) / 1.8;
                Log.d(TAG, "Converted to: " + converted);
                String result = history.getText().toString();
                String output = String.format("F to C: %.1f F = %.1f C %n", fValue, converted);
                history.setText(output + result);
                fahrenheit.setText("");
                celsius.setText(String.format("%.1f", converted));
            }

        } else if (convType == "C") {
            if (cString.equals("")) Toast.makeText(this, "No value entered.", Toast.LENGTH_LONG).show();
            else {
                double cValue = Double.parseDouble(cString);
                Log.d(TAG, "C: " + cValue);
                double converted = (cValue * 1.8) + 32;
                Log.d(TAG, "Converted to: " + converted);
                String result = history.getText().toString();
                String output = String.format("C to F: %.1f C = %.1f F %n", cValue, converted);
                history.setText(output + result);
                celsius.setText("");
                fahrenheit.setText(String.format("%.1f", converted));
            }
        }

    }

    //clears the history and temperature fields
    public void clear(View v) {
        history = findViewById(R.id.display);
        fahrenheit = findViewById(R.id.finput);
        celsius = findViewById(R.id.cinput);

        history.setText("");
        celsius.setText("");
        fahrenheit.setText("");

    }

    //stores field data and conversion type so the data remains on the screen upon rotation
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("FAHRENHEIT",fahrenheit.getText().toString());
        outState.putString("CELSIUS", celsius.getText().toString());
        outState.putString("HISTORY", history.getText().toString());
        outState.putString("CONVTYPE", convType);

        super.onSaveInstanceState(outState);
    }

    //restores data following device rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fahrenheit.setText(savedInstanceState.getString("FAHRENHEIT"));
        celsius.setText(savedInstanceState.getString("CELSIUS"));
        history.setText(savedInstanceState.getString("HISTORY"));
        convType = savedInstanceState.getString("CONVTYPE");
    }


}
