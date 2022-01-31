package com.example.android.fact_check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.android.fact_check.repository.SearchRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ImageSearch extends AsyncTask<Void, Void, Void> {
    Context context;
    ArrayList<String> imgUrlList = new ArrayList<>();
    Search search;
    long start;
    SearchRepository searchRepository = SearchRepository.getInstance(context);
    //Background Thread that connects to the website and searches for image url
    private SimpleIdlingResource idlingResource;
    MutableLiveData<ArrayList<ModelClass>> data;

    public ImageSearch(Context context, Search search, ArrayList<String> imgUrlList, long start) {
        this.context = context;
        this.search = search;
        this.start = start;
        this.imgUrlList = imgUrlList;
    }

    long connection_time_end;


    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Void... voids) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Log.i("response", "operation started");


        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            imgUrlList = new ArrayList<>();
//            website_url = new ArrayList<>();

            for (int i = 0; i < search.claims.size(); i++) {
                if (isCancelled()) {
                    break;
                }
                long connection_time_start = System.currentTimeMillis();
                Document document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url)
                        .ignoreHttpErrors(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                        .cookie("auth", "token").timeout(30_000)
                        .get();

                connection_time_end = System.currentTimeMillis() - connection_time_start;

                Log.i("response", "-------------------------------");
                Log.i("response", "time taken to make connection and parse " + connection_time_end + " ms");
                //   Log.i("response", "document = "+document.toString());
                Elements img = document.select("meta[property=og:image]");
                if (img == null) {
                    if ((img = document.select("meta[property=og:image:secure_url")) == null) {
                        img = document.select("meta[property=twitter:image]");
                    }
                }
                String imgUrl = null;

                if (img != null) {
                    Log.i("response", i + "");
                    imgUrl = img.attr("content");
                    Log.i("response", "url of website :- " + search.claims.get(i).claimReview.get(0).url);
                    Log.i("response", "url of image :- " + imgUrl);
                    imgUrlList.add(imgUrl);
                }
                publishProgress();
            }
            Log.i("response", "Operation took " + (System.currentTimeMillis() - start) / 1000 + " seconds");

        } catch (Exception e) {
            e.printStackTrace();
            searchRepository.setErrorMessage("Some error occurred while getting images");
            Log.v("response-image", "error");
        }

        return null;
    }

    //If connection takes too long displays a toast
    @Override
    protected void onProgressUpdate(Void... values) {
        if (connection_time_end > 4000) {
            Toast.makeText(context, "Search is taking longer than expected", Toast.LENGTH_SHORT).show();
        }
        super.onProgressUpdate(values);

    }

    //This executes after background thread finishes its task
    @Override
    protected void onPostExecute(Void aVoid) {
        searchRepository.setIsUpdating(false);
        try {
            super.onPostExecute(aVoid);
            ArrayList<ArrayList<ModelClass>> searchHistory = searchRepository.getCurrentSearch().getValue();
            if (searchHistory != null) {
                Log.v("response-image-search", "HERE");
                ArrayList<ModelClass> modelClasses = new SearchResult().getModelClass(search, imgUrlList);
                Log.v("response-image-search", modelClasses.size() + "");
                searchHistory.add(modelClasses);
                searchRepository.setErrorMessage("Search finished");
                searchRepository.getCurrentSearch().setValue(searchHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            searchRepository.setErrorMessage("Your search did not match any claims");
            Log.v("response-image", "error");
        }
    }

}
