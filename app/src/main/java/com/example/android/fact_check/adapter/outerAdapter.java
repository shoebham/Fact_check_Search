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
    RecyclerView.RecycledViewPool viewPool;

    public outerAdapter() {
    }

    public outerAdapter(Context context,
                        ArrayList<ArrayList<ModelClass>> supermodel, ArrayList<String> searchHistory) {
        this.context = context;
//        this.models = models;
        this.searchHistory = searchHistory;
        this.supermodel = supermodel;
        viewPool = new RecyclerView.RecycledViewPool();

    }

    @NonNull
    @Override
    public outerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("response-outer-adapter", "here");

        ViewHolder viewHolder = new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.nested_recycler_view_with_cardview, parent, false));
        viewHolder.recyclerView.setRecycledViewPool(viewPool);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull outerAdapter.ViewHolder holder, int position) {
        Log.v("response-outer-adapter", "size:" + supermodel.size());
        for (int i = supermodel.size() - 1; i >= 0; i--) {
            if (position == supermodel.size() - 1 - i) {
                final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setMeasurementCacheEnabled(false);
                layoutManager.setInitialPrefetchItemCount(5);
                holder.recyclerView.setLayoutManager(layoutManager);
                holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        layoutManager.requestLayout();
                    }
                });
                holder.recyclerView.setAdapter(new innerAdapter(context, supermodel.get(i)));
//                holder.textView.setText(searchHistory.get(i));
            }
        }
    }

    @Override
    public int getItemCount() {
        return supermodel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recycler);
            textView = itemView.findViewById(R.id.searched_text);
        }
    }

}
