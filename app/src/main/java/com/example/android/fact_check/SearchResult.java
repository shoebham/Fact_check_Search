package com.example.android.fact_check;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;


interface searchApi {
    Search getSearchResult(String searchText, String language, String resultSize);

    void getImageResult(ArrayList<String> imgUrlList);

    ArrayList<ModelClass> getModelClass(Search search, ArrayList<String> imgUrlList);
}

public class SearchResult implements searchApi {

    Context context;
    long start;
    SearchState searchState = new SearchState();
    Gson gson = new Gson();
    Search search;
    ArrayList<String> imgUrlList = new ArrayList<>();

    public SearchResult() {

    }

    public SearchResult(Context context) {
        this.context = context;
        searchState.isSearching = true;
    }

    public Search getSearchResult(String searchText, String language, String resultSize) {
        start = System.currentTimeMillis();
        RequestQueue queue = Volley.newRequestQueue(context);
        String API_KEY = context.getString(R.string.api_key);
        String url = "https://factchecktools.googleapis.com/v1alpha1/claims:search?languageCode=" +
                language + "&pageSize=" + resultSize + "&query=" + searchText + "&key=" + API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.toString().equals("{}")) {
                            searchState.isSearchValid = false;
                            searchState.error_message = "your search did not match any claims";
                        } else {
                            searchState.isSearchValid = true;
                            search = gson.fromJson(response.toString(), Search.class);
                            setSearch(search);
                            getImageResult(imgUrlList);
                        }
                        searchState.isSearching = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        searchState.isSearchValid = false;
                        searchState.isSearching = false;
                        searchState.error_message = "Some unexpected error occurred";
                    }
                });
        queue.add(jsonObjectRequest);
        return search;
    }

    @Override
    public void getImageResult(ArrayList<String> imgUrlList) {
        ImageSearch imageSearch = new ImageSearch(context, search, imgUrlList, start);
        imageSearch.execute();
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    @Override
    public ArrayList<ModelClass> getModelClass(Search search, ArrayList<String> imgUrlList) {
        ArrayList<ModelClass> models = new ArrayList<>();
        for (int i = 0; i < imgUrlList.size(); i++) {
            ModelClass m = new ModelClass();
            m.setClaim("Claim:- " + search.claims.get(i).text);
            m.setClaimant("Claimant:- " + search.claims.get(i).claimant);
            m.setReview("Factual Rating:- " + search.claims.get(i).claimReview.get(0).textualRating);
            m.setImageUrl(imgUrlList.get(i));
            m.setWebsiteUrl(search.claims.get(i).claimReview.get(0).url);
            Log.i("response", "I am in getMyList() and website url is " + search.claims.get(i).claimReview.get(0).url);
            models.add(m);
        }
        for (ModelClass m : models) {
            Log.v("response-search-result", m.getImageUrl());
        }
        return models;
    }
}