package com.example.android.fact_check.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.fact_check.ImageSearch;
import com.example.android.fact_check.ModelClass;
import com.example.android.fact_check.R;
import com.example.android.fact_check.Search;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchRepository {

    private static SearchRepository instance;
    Search search;
    long start;
    private Context context;
    private ArrayList<ArrayList<ModelClass>> dataSet = new ArrayList<>();
    private Gson gson = new Gson();
    private MutableLiveData<ArrayList<ArrayList<ModelClass>>> data;
    private ArrayList<String> imgUrlList = new ArrayList<>();


    public SearchRepository(Context context) {
        this.context = context;
        data = new MutableLiveData<>();
        Log.i("response-search-repo", "constructor");
    }

    public static SearchRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SearchRepository(context);
        }
        return instance;
    }

    public MutableLiveData<ArrayList<ArrayList<ModelClass>>> getCurrentSearch() {
//        setSearchHistory();
        data.setValue(dataSet);
//        Log.v("response-viewmodel-",data.getValue().size()+"");
        return data;
    }

    public void search(String searchText, String language, String resultSize, SearchRepository searchRepository) {
        search = getSearchResult(searchText, language, resultSize, searchRepository);
    }

    public Search getSearchResult(String searchText,
                                  String language,
                                  String resultSize,
                                  final SearchRepository searchRepository) {
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

                        } else {
                            search = gson.fromJson(response.toString(), Search.class);
                            getImageResult(searchRepository);
//                            setSearchHistory(imgUrlList);
//                            data.postValue(setSearchHistory(imgUrlList));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        data.postValue(null);

                    }
                });
        queue.add(jsonObjectRequest);
        return search;
    }


    public void getImageResult(SearchRepository searchRepository) {
        imgUrlList = new ArrayList<>();
        ImageSearch imageSearch = new ImageSearch(context, search, imgUrlList, start);
        imageSearch.execute();
    }

}
