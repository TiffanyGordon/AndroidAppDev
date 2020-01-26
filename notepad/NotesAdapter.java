package com.ariana.notepad;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "NotesAdapter";
    public String nContent;
    private List<Notes> notesList;
    private MainActivity main;

    NotesAdapter(List<Notes> notesList, MainActivity m) {
        this.notesList = notesList;
        main = m;
    }

    //creates the viewholder for each note to be shown in the recycler view.  Sets click listeners.
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW ViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);

        itemView.setOnClickListener(main);
        itemView.setOnLongClickListener(main);

        return new ViewHolder(itemView);
    }

    //Fills viewholder with note details and trims note content shown to 80 characters
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Note " + position);

        Notes note = notesList.get(position);

        holder.title.setText(note.getTitle());
        holder.timestamp.setText(note.getTimestamp());

        nContent = note.getContent();
        if (nContent.length() > 80) {
            nContent = nContent.substring(0, 80) + "...";
        }
        holder.content.setText(nContent);

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}