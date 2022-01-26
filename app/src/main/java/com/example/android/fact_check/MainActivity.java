package com.example.android.fact_check;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import com.example.android.fact_check.adapter.outerAdapter;
import com.example.android.fact_check.viewmodels.MainActivityViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

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
    private ArrayList<String> imageUrl = new ArrayList<>();
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
    @Nullable
    public SimpleIdlingResource mIdlingResource;
    private MainActivityViewModel mMainActivityViewModel;
    private outerAdapter mAdapter;
    private ArrayList<ModelClass> modelClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Fact Check Search");
        initViews();
        setClickListeners();
//        mAdapter = new outerAdapter();
        supermodel = new ArrayList<ArrayList<ModelClass>>();

        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.init();

        mMainActivityViewModel.getCurrentSearch().observe(this,
                new Observer<ArrayList<ModelClass>>() {
                    @Override
                    public void onChanged(ArrayList<ModelClass> searches) {
                        if (!searches.isEmpty()) {
                            supermodel.add(searches);
                            Log.i("response", supermodel.size() + "super");
                            mAdapter.notifyDataSetChanged();
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
        initRecyclerView();

    }

    public void initViews() {
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
    }

    public void setClickListeners() {
        //Search Button onClickListener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.search(searchText.getText().toString(), language, resultSize);
//                SearchResult searchResult = new SearchResult(getApplicationContext());
//                search = searchResult.getSearchResult(searchText.getText().toString(), language, resultSize);
//                modelClasses = searchResult.getModelClass(search,imageUrl);
//                supermodel.add(modelClasses);
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
//            sendData();
//            initRecyclerView();
            start = System.currentTimeMillis();
//            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
            invalid_search.setVisibility(View.GONE);
            error_text.setVisibility(View.GONE);
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


//    public void sendData() {
//        try {
//            if (mIdlingResource != null) {
//                mIdlingResource.setIdleState(false);
//                System.out.println("midlingresource" + mIdlingResource.isIdleNow());
//            }
//            Log.i("response", "intent get result " + language);
//            Log.i("response", "intent get result " + resultSize);
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String search_text = searchText.getText().toString();
//            searchHistory.add(search_text);
//            String API_KEY = getString(R.string.api_key);
//            String url = "https://factchecktools.googleapis.com/v1alpha1/claims:search?languageCode=" + language + "&pageSize=" + resultSize + "&query=" + search_text + "&key=" + API_KEY;
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.i("response", response.toString());
//                            if (response.toString().equals("{}")) {
////                                toggleVisibility(invalid_search, 0);
////                                toggleVisibility(progressBar, 8);
//                                invalid_search.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.GONE);
//                                Toast.makeText(getApplicationContext(), "your search did not match any claims", Toast.LENGTH_SHORT).show();
//                            } else {
//                                search = gson.fromJson(response.toString(), Search.class);
//                                Log.i("response", search.toString());
//                                new getImage().execute();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.i("response", error.toString());// TODO: Handle error
//                            error.printStackTrace();
//                            progressBar.setVisibility(View.GONE);
//                            error_text.setVisibility(View.VISIBLE);
//
//                            Toast.makeText(getApplicationContext(), "Some unexpected error occurred", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            // Access the RequestQueue through your singleton class.
//            queue.add(jsonObjectRequest);
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Your search did not match any claims", Toast.LENGTH_SHORT).show();
//        }
//    }
//initialise recyclerView
private ArrayList<ArrayList<ModelClass>> demoClass() {
    ArrayList<ArrayList<ModelClass>> demo = new ArrayList<>();
    demo.add(demoModel());
    return demo;
}

    private ArrayList<ModelClass> demoModel() {
        ArrayList<ModelClass> mlist = new ArrayList<>();
        ModelClass m = new ModelClass();
        m.setClaim("a");
        m.setClaimant("B");
        m.setReview("f");
        m.setWebsiteUrl("abcdefg");
        m.setImageUrl("https://i.imgur.com/ZcLLrkY.jpg");
        mlist.add(m);
        return mlist;
    }

    private void initRecyclerView() {
//        supermodel.add(getMyList());
        mAdapter = new outerAdapter(
                getApplicationContext(),
                supermodel,
                mMainActivityViewModel.getCurrentSearch().getValue(), searchHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    //filling recyclerView through modelClass
//    private ArrayList<ModelClass> getMyList() {
//        ArrayList<ModelClass> models = new ArrayList<>();
//        for (int i = 0; i < imageUrl.size(); i++) {
//            ModelClass m = new ModelClass();
//            m.setClaim("Claim:- " + search.claims.get(i).text);
//            m.setClaimant("Claimant:- " + search.claims.get(i).claimant);
//            m.setReview("Factual Rating:- " + search.claims.get(i).claimReview.get(0).textualRating);
//            m.setImageUrl(imageUrl.get(i));
//            m.setWebsiteUrl(website_url.get(i));
//            Log.i("response", "I am in getMyList() and website url is " + website_url);
//            models.add(m);
//        }
//        return models;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        System.out.println("mIdlingResource" + mIdlingResource.isIdleNow());
        return mIdlingResource;
    }

    //This is where the most of the work of the App is done
    //getting Image by scraping image from the url
//    public class getImage extends AsyncTask<Void, Void, Void> {
//        //Background Thread that connects to the website and searches for image url
//        private SimpleIdlingResource idlingResource;
//
//        @SuppressLint("WrongThread")
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//
//            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//            Log.i("response", "operation started");
//            try {
//                imageUrl = new ArrayList<>();
//                website_url = new ArrayList<>();
//
//                for (int i = 0; i < search.claims.size(); i++) {
//                    if (isCancelled()) {
//                        break;
//                    }
//
//
//                    connection_time_start = System.currentTimeMillis();
//                    document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url).ignoreHttpErrors(true)
//                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
//                            .cookie("auth", "token").timeout(30_000)
//                            .get();
//                    connection_time_end = System.currentTimeMillis() - connection_time_start;
//
//                    Log.i("response", "-------------------------------");
//                    Log.i("response", "time taken to make connection and parse " + connection_time_end + " ms");
//                    //   Log.i("response", "document = "+document.toString());
//                    img = document.select("meta[property=og:image]");
//                    if (img == null) {
//                        if ((img = document.select("meta[property=og:image:secure_url")) == null) {
//                            img = document.select("meta[property=twitter:image]");
//                        }
//                    }
//                    imgUrl = null;
//
//                    if (img != null) {
//                        Log.i("response", i + "");
//                        imgUrl = img.attr("content");
//                        Log.i("response", "url of website :- " + search.claims.get(i).claimReview.get(0).url);
//                        Log.i("response", "url of image :- " + imgUrl);
//                        imageUrl.add(imgUrl);
//                        website_url.add(search.claims.get(i).claimReview.get(0).url);
//                    }
//                    publishProgress();
//                }
//                Log.i("response", "Operation took " + (System.currentTimeMillis() - start) / 1000 + " seconds");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Some Strange Error Occurred", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            return null;
//        }
//
//        //If connection takes too long displays a toast
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//
//            if (connection_time_end > 5000) {
//                Toast.makeText(getApplicationContext(), "This is taking longer than expected.\nThis usually happens due to network problems.\n If this continues try changing settings.", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        //This executes after background thread finishes its task
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            try {
//
//                initRecyclerView();
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Search Finished.", Toast.LENGTH_LONG).show();
//                if (mIdlingResource != null) {
//                    mIdlingResource.setIdleState(true);
//                }
//                super.onPostExecute(aVoid);
//
//
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "Your search did not match any claims", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
}
