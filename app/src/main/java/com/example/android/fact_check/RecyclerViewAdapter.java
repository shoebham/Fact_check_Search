package com.example.android.fact_check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    Context context;
    ArrayList<ModelClass> models;

    public RecyclerViewAdapter(Context context, ArrayList<ModelClass> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.mClaim.setText(models.get(position).getClaim());
        holder.mClaimant.setText(models.get(position).getClaimant());
        holder.mReview.setText(models.get(position).getReview());
//        holder.mImageView.setImageResource(models.get(position).getImg());
        GlideApp.with(context).asBitmap().load(models.get(position).getImageUrl().get(position)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }


    }
}
