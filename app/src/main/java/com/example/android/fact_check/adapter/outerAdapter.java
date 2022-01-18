package com.example.android.fact_check.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.fact_check.ModelClass;
import com.example.android.fact_check.R;

import java.util.ArrayList;

public class outerAdapter extends RecyclerView.Adapter<outerAdapter.ViewHolder> {
    Context context;
    ArrayList<ModelClass> models;
    ArrayList<String> searchHistory;
    ArrayList<ArrayList<ModelClass>> supermodel;

    public outerAdapter(Context context, ArrayList<ArrayList<ModelClass>> supermodel, ArrayList<ModelClass> models, ArrayList<String> searchHistory) {
        Log.v("Recycler", models.get(0).getClaim());
        this.context = context;
        this.models = models;
        this.searchHistory = searchHistory;
        this.supermodel = supermodel;
    }

    @NonNull
    @Override
    public outerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.nested_recycler_view_with_cardview, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull outerAdapter.ViewHolder holder, int position) {
        Log.v("recycler", "size:" + searchHistory.size());
        for (int i = searchHistory.size() - 1; i >= 0; i--) {
            if (position == searchHistory.size() - 1 - i) {
                final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setMeasurementCacheEnabled(false);
                holder.recyclerView.setLayoutManager(layoutManager);
                holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        layoutManager.requestLayout();
                    }
                });
                holder.recyclerView.setAdapter(new innerAdapter(context, supermodel.get(i)));
                holder.textView.setText(searchHistory.get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        return searchHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.outer_recycler);
            textView = itemView.findViewById(R.id.search_text);
        }
    }

}