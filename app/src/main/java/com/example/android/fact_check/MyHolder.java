package com.example.android.fact_check;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder {
    ImageView mImageView;
    TextView mClaim, mClaimant, mReview;

    public MyHolder(@NonNull View itemView) {
        super(itemView);
        this.mImageView = itemView.findViewById(R.id.image);
        this.mClaim = itemView.findViewById(R.id.claim);
        this.mClaimant = itemView.findViewById(R.id.claimant);
        this.mReview = itemView.findViewById(R.id.review);

    }


}
