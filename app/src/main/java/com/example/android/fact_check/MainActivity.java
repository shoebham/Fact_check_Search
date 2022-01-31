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
    private int lastSize;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Fact Check Search");
        initViews();
        setClickListeners();
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.init();

        lastSize = mMainActivityViewModel.getCurrentSearch().getValue().size() - 1;
        mMainActivityViewModel.getCurrentSearch().observe(this,
                new Observer<ArrayList<ArrayList<ModelClass>>>() {
                    @Override
                    public void onChanged(ArrayList<ArrayList<ModelClass>> searches) {
                        if (!searches.isEmpty()) {
                            supermodel = searches;
                            Log.i("response", supermodel.size() + " super");
                            hideThingsAfterSearch();
                            recyclerView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
        mMainActivityViewModel.getIsUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        mMainActivityViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                hideThingsAfterSearch();
                if (recyclerView.getVisibility() != View.VISIBLE) {
                    if (s.equals("Your search did not match any claims")) {
                        invalid_search.setVisibility(View.VISIBLE);
                    } else if (s.equals("Some error occurred while getting images")) {
                        error_text.setVisibility(View.VISIBLE);
                    }
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
        initRecyclerView();

    }

    protected void hideThingsAfterSearch() {
        invalid_search.setVisibility(View.GONE);
        error_text.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
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
//                    showAndHideThingsOnSearch();
                    button.callOnClick();
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
            start = System.currentTimeMillis();
            emptyText.setVisibility(View.GONE);
            invalid_search.setVisibility(View.GONE);
            error_text.setVisibility(View.GONE);
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


//initialise recyclerView
    private void initRecyclerView() {
//        supermodel.add(getMyList());
        mAdapter = new outerAdapter(
                getApplicationContext(),
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

}
