package com.ariana.notepad;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView timestamp;
    TextView content;

    ViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        timestamp = view.findViewById(R.id.timestamp);
        content = view.findViewById(R.id.content);
    }

}
