package com.ariana.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String SADAPT = "stockAdapter";
    private List<Stocks> stocksList;
    private MainActivity mainAct;

    StockAdapter(List<Stocks> sList, MainActivity main) {
        this.stocksList = sList;
        mainAct = main;
    }

    @NonNull
    @Override
    //Creates viewholder for recycler items and sets click listeners
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(SADAPT, "onCreateViewHolder: MAKING NEW ViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new ViewHolder(itemView);
    }

    @Override
    //Sets stock details in viewholder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(SADAPT, "onBindViewHolder: FILLING VIEW HOLDER Stock " + position);

        Stocks stock = stocksList.get(position);

        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.value.setText(stock.getValue().toString());

        String change = String.format("%.2f", stock.getChange());
        String percent = String.format("%.2f", stock.getPercent());
        if (Double.valueOf(change) >= 0) {
            holder.change.setText("▲" + change + " (" + percent + ")");
            holder.symbol.setTextColor(Color.parseColor("#28de40"));
            holder.name.setTextColor(Color.parseColor("#28de40"));
            holder.value.setTextColor(Color.parseColor("#28de40"));
            holder.change.setTextColor(Color.parseColor("#28de40"));
        } else {
            holder.change.setText("▼" + change + " (" + percent + ")");
            holder.symbol.setTextColor(Color.parseColor("#ed1515"));
            holder.name.setTextColor(Color.parseColor("#ed1515"));
            holder.value.setTextColor(Color.parseColor("#ed1515"));
            holder.change.setTextColor(Color.parseColor("#ed1515"));
        }


    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }

}
