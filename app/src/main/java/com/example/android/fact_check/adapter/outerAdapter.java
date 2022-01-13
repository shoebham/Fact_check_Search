package com.example.android.fact_check.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.fact_check.ModelClass;
import com.example.android.fact_check.R;

import java.util.ArrayList;

public class outerAdapter extends RecyclerView.Adapter<outerAdapter.ViewHolder> {
    Context context;
    ArrayList<ModelClass> models;

    public outerAdapter(Context context, ArrayList<ModelClass> models) {
        Log.v("Recycler", models.get(0).getClaim());
        this.context = context;
        this.models = models;
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
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        if (position == 0)
            holder.recyclerView.setAdapter(new innerAdapter(context, models));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.outer_recycler);
        }
    }

}
