package com.example.android.fact_check;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class MainActivity extends AppCompatActivity {

    private Search search;
    public SearchView searchView;
    public EditText EditText;
    public TextView claim_text;
    public TextView claimaint_text;
    public TextView review_text;
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
    //    public ArrayList<Map> claims;
//    public Map<String,String> responseBody;
//    public Map<String,String> text;
//    public Map<String,String> claimant;
//    public Map<String,String> claimDate;
//    public ArrayList<Map> claimReview;
//    public Map<String,ArrayList> publisher;
//    public ArrayList<Map> publisherInfo;
//    public Map<String,String> name;
//    public Map<String,String> site;
//    public Map<String,String> url;
//    public Map<String,String> title;
//    public Map<String,String> textualRating;
//    public Map<String,String> languageCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText = findViewById(R.id.text);
        claim_text = findViewById(R.id.claim);
        claimaint_text = findViewById(R.id.claimaint);
        review_text = findViewById(R.id.review);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.image);
        cardView = findViewById(R.id.card);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(search.claims.get(0).claimReview.get(0).url));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intent);
//            }
//        });


    }

    public JSONArray jsonArray = new JSONArray();
    public JSONObject jsonObject = new JSONObject();
    public Gson gson = new Gson();

    public void sendData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String search_text = EditText.getText().toString();
        String url = "https://factchecktools.googleapis.com/v1alpha1/claims:search?query=" + search_text + "&key=AIzaSyB2Krqs92spjiNKQL9NApU6uykAWVyBtcE";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response", response.toString());
                        search = gson.fromJson(response.toString(), Search.class);
                        Log.i("response", search.toString());
                        new getImage().execute();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("response", error.toString());// TODO: Handle error

                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
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
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(this, getMyList());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ArrayList<ModelClass> getMyList() {
        ArrayList<ModelClass> models = new ArrayList<>();

        for (int i = 0; i < search.claims.size(); i++) {
            ModelClass m = new ModelClass();
            m.setClaim("Claim:- " + search.claims.get(i).text);
            m.setClaimant("Claimant:- " + search.claims.get(i).claimant);
            m.setReview("Factual Rating:-" + search.claims.get(i).claimReview.get(0).textualRating);
            m.setImageUrl(imageUrl);
            models.add(m);
//            claim_text.setText("Claim:- " + search.claims.get(i).text);
//            claimaint_text.setText("Claimant:- " + search.claims.get(i).claimant);
//            review_text.setText("Factual Rating:-" + search.claims.get(i).claimReview.get(0).textualRating);

        }
        return models;
    }

    private void update() {
        claim_text.setText("Claim:- " + search.claims.get(0).text);
        claimaint_text.setText("Claimant:- " + search.claims.get(0).claimant);
        review_text.setText("Factual Rating:-" + search.claims.get(0).claimReview.get(0).textualRating);
    }

    private class getImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                imageUrl = new ArrayList<>();
                for (int i = 0; i < search.claims.size(); i++) {
                    document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url).get();
                    img = document.select("meta[property=og:image]");
                    imgUrl = null;
                    if (img != null) {
                        imgUrl = img.attr("content");
                        Log.i("response", imgUrl);

                        imageUrl.add(imgUrl);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            GlideApp.with(getApplicationContext()).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            initRecyclerView();
            super.onPostExecute(aVoid);
        }
    }
}
