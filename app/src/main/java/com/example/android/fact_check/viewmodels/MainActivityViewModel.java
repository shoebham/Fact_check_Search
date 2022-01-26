package com.example.android.fact_check.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.fact_check.ModelClass;
import com.example.android.fact_check.repository.SearchRepository;

import java.util.ArrayList;

public class MainActivityViewModel extends AndroidViewModel {

    Application application;
    private MutableLiveData<ArrayList<ModelClass>> currentSearch;
    private SearchRepository searchRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void init() {
        if (currentSearch != null) {
            return;
        }
        searchRepository = SearchRepository.getInstance(application.getApplicationContext());
        currentSearch = searchRepository.getCurrentSearch();
    }

    public LiveData<ArrayList<ModelClass>> getCurrentSearch() {
        return currentSearch;
    }

    public void search(String searchText, String language, String resultSize) {
        searchRepository.search(searchText, language, resultSize);
    }
//    public LiveData<ArrayList<ArrayList<ModelClass>>> getSearchHistory
//            (String toString, String language, String resultSize) {
//
//        return mSearchHistory;
//    }
}
