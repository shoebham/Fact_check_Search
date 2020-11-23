package com.example.android.fact_check;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    Context context;
    ArrayList<ModelClass> models;
    View view;

    public RecyclerViewAdapter(Context context, ArrayList<ModelClass> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        RequestOptions myOptions = new RequestOptions()
                .override(700, 700);
        holder.mClaim.setText(models.get(position).getClaim());
        holder.mClaimant.setText(models.get(position).getClaimant());
        holder.mReview.setText(models.get(position).getReview());
        long start = System.currentTimeMillis();
        GlideApp.with(context).applyDefaultRequestOptions(myOptions).load(models.get(position).getImageUrl()).into(holder.mImageView);
        long end = System.currentTimeMillis();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(models.get(position).getWebsiteUrl()));
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }


    }
}
