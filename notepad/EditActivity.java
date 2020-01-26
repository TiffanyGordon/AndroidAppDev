package com.ariana.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText title;
    private EditText content;
    private String pos;
    private static final String CHECK = "Container";

    //Loads editing template and loads previously saved information if called to edit an existing note
    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.noteTitle);
        content = findViewById(R.id.noteContent);

        Intent intent = getIntent();
        if (intent.hasExtra("TITLE")) {
            String titleText = intent.getStringExtra("TITLE");
            title.setText(titleText);
            String contentText = intent.getStringExtra("CONTENT");
            content.setText(contentText);
            pos = intent.getStringExtra("POSITION");
        }

        Log.d(CHECK, String.format("onActivityResult: EDIT data: %s %s %s", title, content, pos));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.savemenu, menu);
        return true;
    }

    //Saves the created note
    public boolean onOptionsItemSelected(MenuItem item) {

        title = findViewById(R.id.noteTitle);
        content = findViewById(R.id.noteContent);
        long noteTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd, HH:mm a");
        Date date = new Date(noteTimestamp);
        String time = sdf.format(date);

        if (TextUtils.isEmpty(title.getText())) {
            Toast.makeText(this, "Notes must have a title to be saved.", Toast.LENGTH_LONG).show();
            Intent data = new Intent();
            finish();
        }

        else {
            Intent data = new Intent();
            data.putExtra("TITLE", title.getText().toString());
            data.putExtra("CONTENT", content.getText().toString());
            data.putExtra("TIMESTAMP", time);
            data.putExtra("POSITION", pos);
            setResult(RESULT_OK, data);
            finish();
        }

        return true;
    }


    //Creates a dialogue when the back button is pressed, asking the user if the note should be saved
        @Override
        public void onBackPressed() {

            title = findViewById(R.id.noteTitle);
            String titleText = title.getText().toString();

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setPositiveButton("YES",
                   new DialogInterface.OnClickListener() {

                       public void onClick(DialogInterface dialog, int id) {

                           content = findViewById(R.id.noteContent);
                           long noteTimestamp = System.currentTimeMillis();
                           SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd, HH:mm a");
                           Date date = new Date(noteTimestamp);
                           String time = sdf.format(date);

                           Intent data = new Intent();
                           data.putExtra("TITLE", title.getText().toString());
                           data.putExtra("CONTENT", content.getText().toString());
                           data.putExtra("TIMESTAMP", time);
                           data.putExtra("POSITION", pos);
                           setResult(RESULT_OK, data);
                           finish();
                       }});
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }});
            builder.setMessage(String.format("Your note is not saved! \n Save note '%s?'", titleText));

            AlertDialog dialog = builder.create();
            dialog.show();
        }
}
