package com.example.android.fact_check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.fact_check.adapter.outerAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {


    public EditText searchText;//pageSize;
    public RecyclerViewAdapter recyclerViewAdapter;
    public TextView claim_text;
    public TextView claimant_text;
    public TextView review_text;
    public String language;
    public String resultSize = "5";
    public Document document;
    public Elements img;
    public String imgUrl;
    public Button button;
    public ImageView imageView;
    public MaterialCardView cardView;
    public ImageButton parameters;
    public Search search;
    private TextView emptyText;
    private TextView invalid_search;
    private TextView error_text;
    private RecyclerView recyclerView;
    private ArrayList<String> imageUrl;
    private ProgressBar progressBar;
    private Gson gson = new Gson();
    private ArrayList<String> website_url;
    private Spinner spinner;
    private long start;
    private OkHttpClient okHttpClient;
    private TextView long_time_text;
    private long connection_time_start, connection_time_end;
    RecyclerView verticalRecyclerView;
    outerAdapter adapter;
    public ArrayList<String> searchHistory;
    public ArrayList<ArrayList<ModelClass>> supermodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Fact Check Search");
        searchHistory = new ArrayList<>();
        supermodel = new ArrayList<ArrayList<ModelClass>>();
        searchText = findViewById(R.id.search_text);
        error_text = findViewById(R.id.error_text);
        claim_text = findViewById(R.id.claim);
        claimant_text = findViewById(R.id.claimant);
        review_text = findViewById(R.id.review);
        button = findViewById(R.id.search_button);
        imageView = findViewById(R.id.image);
        cardView = findViewById(R.id.card);
        progressBar = findViewById(R.id.progressbar);
        emptyText = findViewById(R.id.empty_text);
        invalid_search = findViewById(R.id.invalid_search);
        recyclerView = findViewById(R.id.recycler_view);
        parameters = findViewById(R.id.parameters);
        long_time_text = findViewById(R.id.long_time_text);
        //Search Button onClickListener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndHideThingsOnSearch();
            }
        });

        //settings icon onClickListener
        parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), parameters.class);
                intent.putExtra("language", language);
                intent.putExtra("resultSize", resultSize);
                startActivityForResult(intent, 1);
            }
        });
        Log.i("response", "intent get result " + resultSize);

        //Search Bar OnClickListener which shows keyboard
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    showAndHideThingsOnSearch();
                    searchText.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }


    //    hides and shows things like progress bar etc
    protected void showAndHideThingsOnSearch() {
        if (!searchText.getText().toString().equals("")) {
            sendData();
            start = System.currentTimeMillis();
            toggleVisibility(recyclerView, 8);
            toggleVisibility(emptyText, 8);
            toggleVisibility(invalid_search, 8);
            toggleVisibility(error_text, 8);
            toggleVisibility(progressBar, 0);
//            recyclerView.setVisibility(View.GONE);
//            emptyText.setVisibility(View.GONE);
//            invalid_search.setVisibility(View.GONE);
//            error_text.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Loading...\n Please wait...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Enter Some Text", Toast.LENGTH_SHORT).show();
        }
    }
    //Sending Data to settings activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("response", "i am here");
        if (resultCode == RESULT_OK && requestCode == 1) {
            language = data.getStringExtra("language");
            resultSize = data.getStringExtra("resultSize");
            Log.i("response", "intent get result " + language);
            Log.i("response", "intent get result " + resultSize);
        }
    }
    //sending data to the API

    public void toggleVisibility(View view, int visibility) {
//        invisible-4
//        visible-0
//        gone-8
        view.setVisibility(visibility);
    }

    public void sendData() {
        try {
            Log.i("response", "intent get result " + language);
            Log.i("response", "intent get result " + resultSize);
            RequestQueue queue = Volley.newRequestQueue(this);
            String search_text = searchText.getText().toString();
            searchHistory.add(search_text);
            String API_KEY = getString(R.string.api_key);
            String url = "https://factchecktools.googleapis.com/v1alpha1/claims:search?languageCode=" + language + "&pageSize=" + resultSize + "&query=" + search_text + "&key=" + API_KEY;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("response", response.toString());
                            if (response.toString().equals("{}")) {
                                toggleVisibility(invalid_search, 0);
                                toggleVisibility(progressBar, 8);
//                                invalid_search.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.GONE);
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
                            error.printStackTrace();
                            toggleVisibility(progressBar, 0);
                            toggleVisibility(error_text, 8);
//                            progressBar.setVisibility(View.GONE);
//                            error_text.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(), "Some unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Your search did not match any claims", Toast.LENGTH_SHORT).show();
        }
    }
    //initialise recyclerView

    private void initRecyclerView() {
        Log.v("Recycler", "214");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        supermodel.add(getMyList());
        toggleVisibility(recyclerView, 0);
//        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new outerAdapter(getApplicationContext(), supermodel, getMyList(), searchHistory));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    //filling recyclerView through modelClass
    private ArrayList<ModelClass> getMyList() {
        ArrayList<ModelClass> models = new ArrayList<>();
        for (int i = 0; i < imageUrl.size(); i++) {
            ModelClass m = new ModelClass();
            m.setClaim("Claim:- " + search.claims.get(i).text);
            m.setClaimant("Claimant:- " + search.claims.get(i).claimant);
            m.setReview("Factual Rating:- " + search.claims.get(i).claimReview.get(0).textualRating);
            m.setImageUrl(imageUrl.get(i));
            m.setWebsiteUrl(website_url.get(i));
            Log.i("response", "I am in getMyList() and website url is " + website_url);
            models.add(m);

        }
        return models;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //This is where the most of the work of the App is done
    //getting Image by scraping image from the url
    class getImage extends AsyncTask<Void, Void, Void> {

        //Background Thread that connects to the website and searches for image url
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.i("response", "operation started");
            try {
                imageUrl = new ArrayList<>();
                website_url = new ArrayList<>();

                for (int i = 0; i < search.claims.size(); i++) {
                    if (isCancelled()) {
                        break;
                    }


                    connection_time_start = System.currentTimeMillis();
                    document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url).ignoreHttpErrors(true)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                            .cookie("auth", "token").timeout(30_000)
                            .get();
                    connection_time_end = System.currentTimeMillis() - connection_time_start;

                    Log.i("response", "-------------------------------");
                    Log.i("response", "time taken to make connection and parse " + connection_time_end + " ms");
                    //   Log.i("response", "document = "+document.toString());
                    img = document.select("meta[property=og:image]");
                    if (img == null) {
                        if ((img = document.select("meta[property=og:image:secure_url")) == null) {
                            img = document.select("meta[property=twitter:image]");
                        }
                    }
                    imgUrl = null;

                    if (img != null) {
                        Log.i("response", i + "");
                        imgUrl = img.attr("content");

                        Log.i("response", "url of website :- " + search.claims.get(i).claimReview.get(0).url);
                        Log.i("response", "url of image :- " + imgUrl);
                        imageUrl.add(imgUrl);
                        website_url.add(search.claims.get(i).claimReview.get(0).url);
                    }
                    publishProgress();
                }
                Log.i("response", "Operation took " + (System.currentTimeMillis() - start) / 1000 + " seconds");

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Some Strange Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        //If connection takes too long displays a toast
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (connection_time_end > 5000) {
                Toast.makeText(getApplicationContext(), "This is taking longer than expected.\nThis usually happens due to network problems.\n If this continues try changing settings.", Toast.LENGTH_LONG).show();
            }
//            initRecyclerView();
        }

        //This executes after background thread finishes its task
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                initRecyclerView();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Search Finished.", Toast.LENGTH_LONG).show();
                super.onPostExecute(aVoid);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Your search did not match any claims", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
