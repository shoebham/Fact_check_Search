package com.example.android.fact_check.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.android.fact_check.GlideApp;
import com.example.android.fact_check.ModelClass;
import com.example.android.fact_check.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class innerAdapter extends RecyclerView.Adapter<innerAdapter.ViewHolder> {

    ArrayList<ModelClass> models;
    View view;
    Context context;
    public AtomicInteger count = new AtomicInteger(0);

    public innerAdapter(Context context, ArrayList<ModelClass> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("response-inner-adapter", "count:- " + count.incrementAndGet());
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inner_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            @SuppressLint("RecyclerView") final int position) {

        holder.mClaim.setText(models.get(position).getClaim());
        Log.v("Recycler", "45" + models.get(position).getClaim());
        holder.mReview.setText(models.get(position).getReview());
        RequestOptions myOptions = new RequestOptions()
                .override(700, 700);
        long start = System.currentTimeMillis();
        GlideApp.with(context).applyDefaultRequestOptions(myOptions).load(models.get(position).getImageUrl()).into(holder.mImageView);
        long end = System.currentTimeMillis();
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(models.get(position).getWebsiteUrl()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mClaim, mReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mClaim = itemView.findViewById(R.id.claim);
            mReview = itemView.findViewById(R.id.review);
        }
    }
}
