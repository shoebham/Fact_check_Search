package com.example.android.fact_check;

import java.util.ArrayList;

public class Search {
    public ArrayList<claims> claims;
    public String nextPageToken;
}

class claims {
    public String text;
    public String claimant;
    public String claimDate;
    public ArrayList<claimReview> claimReview;
}

class publisher {
    public String name;
    public String site;
}

class claimReview {
    public publisher publisher;
    public String url;
    public String title;
    public String reviewDate;
    public String textualRating;
    public String languageCode;
}