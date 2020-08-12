package com.example.android.fact_check;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private Search search;
    public SearchView searchView;
    public EditText searchText, pageSize;
    public TextView claim_text;
    public TextView claimaint_text;
    public TextView review_text;
    public TextView emptyText;
    public TextView invaild_search;
    public Button button;
    public ImageView imageView;
    public Bitmap mIcon_val;
    public Document document;
    public Elements img;
    public String imgUrl;
    public MaterialCardView cardView;
    public RecyclerView recyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;
    public ArrayList<String> imageUrl;
    public ProgressBar progressBar;
    public JSONArray jsonArray = new JSONArray();
    public JSONObject jsonObject = new JSONObject();
    public Gson gson = new Gson();
    public int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchText = findViewById(R.id.text);
        claim_text = findViewById(R.id.claim);
        claimaint_text = findViewById(R.id.claimaint);
        review_text = findViewById(R.id.review);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.image);
        cardView = findViewById(R.id.card);
        progressBar = findViewById(R.id.progressbar);
        pageSize = findViewById(R.id.pageSize);
        emptyText = findViewById(R.id.empty_text);
        invaild_search = findViewById(R.id.invaild_search);
        recyclerView = findViewById(R.id.recycler_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
                recyclerView.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Loading...\n please wait...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendData() {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String search_text = searchText.getText().toString();
            String page_Size = pageSize.getText().toString();
            String url = "https://factchecktools.googleapis.com/v1alpha1/claims:search?pageSize=" + page_Size + "&query=" + search_text + "&key=AIzaSyB2Krqs92spjiNKQL9NApU6uykAWVyBtcE";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("response", response.toString());
                            if (response.toString().equals("{}")) {
                                invaild_search.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "your search did not match any claims", Toast.LENGTH_SHORT).show();
                            } else {

                                search = gson.fromJson(response.toString(), Search.class);
                                Log.i("response", search.toString());
                                new getImage().execute();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("response", error.toString());// TODO: Handle error
                            Toast.makeText(getApplicationContext(), "Some unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });

// Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "your search did not match any claims", Toast.LENGTH_SHORT).show();
        }
    }

    class Search {
        private ArrayList<claims> claims;
        private String nextPageToken;

    }

    class claims {
        private String text;
        private String claimant;
        private String claimDate;
        private ArrayList<claimReview> claimReview;
    }

    class claimReview {
        private publisher publisher;
        private String url;
        private String title;
        private String reviewDate;
        private String textualRating;
        private String languageCode;
    }

    class publisher {
        private String name;
        private String site;
    }

    private void initRecyclerView() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewAdapter = new RecyclerViewAdapter(this, getMyList());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    private ArrayList<ModelClass> getMyList() {
        ArrayList<ModelClass> models = new ArrayList<>();
        for (int i = 0; i < search.claims.size(); i++) {
            ModelClass m = new ModelClass();
            m.setClaim("Claim:- " + search.claims.get(i).text);
            m.setClaimant("Claimant:- " + search.claims.get(i).claimant);
            m.setReview("Factual Rating:-" + search.claims.get(i).claimReview.get(0).textualRating);
            m.setImageUrl(imageUrl.get(i));
            models.add(m);
//            claim_text.setText("Claim:- " + search.claims.get(i).text);
//            claimaint_text.setText("Claimant:- " + search.claims.get(i).claimant);
//            review_text.setText("Factual Rating:-" + search.claims.get(i).claimReview.get(0).textualRating);
        }
        return models;
    }

    long start;
    private class getImage extends AsyncTask<Void, Void, Void> {
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            start = System.currentTimeMillis();
            Log.i("response", "operation started");
            try {
                imageUrl = new ArrayList<>();
                for (int i = 0; i < search.claims.size(); i++) {
                    document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url)
                            .cookie("auth", "token")
                            .get();
                    //   Log.i("response", "document = "+document.toString());
                    img = document.select("meta[property=og:image]");
                    if (img == null) {
                        if ((img = document.select("meta[property=og:image:secure_url")) == null) {
                            img = document.select("meta[property=twitter:image]");
                        }
                        ;
                    }
                    imgUrl = null;
                    Log.i("response", i + "");
                    if (img != null) {
                        imgUrl = img.attr("content");
                        Log.i("response", imgUrl);
                        imageUrl.add(imgUrl);

                    }

                }
                Log.i("response", "Operation took " + (System.currentTimeMillis() - start) / 1000 + " seconds");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Some unexpected error occurred", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                initRecyclerView();
                super.onPostExecute(aVoid);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "your search did not match any claims", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
