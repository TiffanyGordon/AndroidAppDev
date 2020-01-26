package com.ariana.stockwatch;

import android.widget.TextView;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView value;
    TextView name;
    TextView change;

    ViewHolder(View view) {
        super(view);
        symbol = view.findViewById(R.id.symbol);
        value = view.findViewById(R.id.value);
        name = view.findViewById(R.id.name);
        change = view.findViewById(R.id.change);
    }
}
