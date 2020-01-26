package com.ariana.notepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter noteAdapter;

    private static final int ADD_CODE = 111;
    private static final int EDIT_CODE = 222;
    private EditText title;
    private EditText content;
    private String timestamp;
    private TextView showTitle;

    private static final String CHECK = "Container";
    private static final String TAG = "JSON";

    //Restores saved instance state and loads previously saved notes from stored file
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);

        noteAdapter = new NotesAdapter(notesList, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.noteTitle);
        content = findViewById(R.id.noteContent);
        long noteTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd, HH:mm a");
        Date date = new Date(noteTimestamp);
        timestamp = sdf.format(date);

        showTitle = findViewById(R.id.title);

        loadFile();
        setTitle(String.format("NotePad (%d)", notesList.size()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    //loads previously saved notes from file to show in recycler view
    public void loadFile() {

        notesList.clear();
        try {
            InputStream inputStream = openFileInput("Notes.txt");

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
                    Log.d(TAG, "doRead: " + jsonArray.length());

                    for (int i = 0; i <= jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("noteTitle");
                        String content = jsonObject.getString("noteContent");
                        String timestamp = jsonObject.getString("noteTimestamp");
                        Notes note = new Notes(title, content, timestamp);
                        notesList.add(note);
                    }

                    Log.d(TAG, "loadFile: " + notesList);
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

    //writes notes to file
    protected void onPause() {

       try {
           Log.d(TAG, "saveNotes: Saving JSON File");


           FileOutputStream fos = openFileOutput("Notes.txt", Context.MODE_PRIVATE);

           JSONArray jsonArray = new JSONArray();

           for (Notes note : notesList) {
               try {
                   JSONObject noteJSON = new JSONObject();
                   noteJSON.put("noteTitle", note.getTitle());
                   noteJSON.put("noteContent", note.getContent());
                   noteJSON.put("noteTimestamp", note.getTimestamp());

                   jsonArray.put(noteJSON);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

           String jsonText = jsonArray.toString();
           Log.d(TAG, "saveNotes: JSON: " + jsonText);

           fos.write(jsonText.getBytes());
           fos.close();
       } catch (IOException e) {
           Toast.makeText(this, "Error on Save", Toast.LENGTH_SHORT).show();

       }
        super.onPause();
    }

    //opens EditActivity upon short click on item with selected note title and content filled.
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Notes note = notesList.get(pos);

        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("TITLE", note.getTitle());
        intent.putExtra("CONTENT", note.getContent());
        intent.putExtra("POSITION", String.valueOf(pos));

        this.startActivityForResult(intent, EDIT_CODE);
    }

    //opens a dialogue asking if selected note should be deleted upon long click
    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Notes note = notesList.get(pos);
        Log.d(CHECK, "Note Content: " + note);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        notesList.remove(pos);
                        noteAdapter.notifyDataSetChanged();
                        setTitle(String.format("NotePad (%d)", notesList.size()));
                    }
                });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setMessage(String.format("Delete note '%s'?", note.getTitle()));

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
                }

    //Creates main menu with info and add note options
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.info:
                Intent intent = new Intent(this, AboutActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.add:
                intent = new Intent(this, EditActivity.class);
                this.startActivityForResult(intent, ADD_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Modifies data set based on information passed back from add or edit note activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CODE) {
            if (resultCode == RESULT_OK) {
                String titleText = data.getStringExtra("TITLE");
                String contentText = data.getStringExtra("CONTENT");
                String timeText = data.getStringExtra("TIMESTAMP");

                notesList.add(0, new Notes(titleText, contentText, timeText));
                noteAdapter.notifyDataSetChanged();

                setTitle(String.format("NotePad (%d)", notesList.size()));

                Log.d(CHECK, String.format("onActivityResult: ADD data: %s %s %s", titleText, contentText, timeText));
            } else {
                Log.d(CHECK, "onActivityResult: result Code: " + resultCode);
            }

        } else if (requestCode == EDIT_CODE) {
            if (resultCode == RESULT_OK) {
                String titleText = data.getStringExtra("TITLE");
                String contentText = data.getStringExtra("CONTENT");
                String timeText = data.getStringExtra("TIMESTAMP");
                String pos = data.getStringExtra("POSITION");

                notesList.remove(Integer.parseInt(pos));
                noteAdapter.notifyDataSetChanged();
                notesList.add(0, new Notes(titleText, contentText, timeText));
                noteAdapter.notifyDataSetChanged();

                Log.d(CHECK, String.format("onActivityResult: EDIT data: %s %s %s %s", titleText, contentText, timeText, pos));
            } else {
                Log.d(CHECK, "onActivityResult: result Code: " + resultCode);
            }
        }
        else {
            Log.d(CHECK, "onActivityResult: Request Code " + requestCode);
        }
    }

}
