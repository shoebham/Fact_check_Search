package com.example.android.fact_check.adapter.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.fact_check.R;

public class MyHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public TextView mClaim, mClaimant, mReview, mSearchText;

    public MyHolder(@NonNull View itemView) {
        super(itemView);
        this.mImageView = itemView.findViewById(R.id.image);
        this.mClaim = itemView.findViewById(R.id.claim);
        this.mClaimant = itemView.findViewById(R.id.claimant);
        this.mReview = itemView.findViewById(R.id.review);
    }


}
