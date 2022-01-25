package com.example.android.fact_check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ImageSearch extends AsyncTask<Void, Void, Void> {
    Context context;
    ArrayList<String> imgUrlList;
    Search search;
    long start;
    //Background Thread that connects to the website and searches for image url
    private SimpleIdlingResource idlingResource;

    ImageSearch(Context context, Search search, ArrayList<String> imgUrlList, long start) {
        this.context = context;
        this.search = search;
        this.start = start;
        this.imgUrlList = imgUrlList;
    }

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Void... voids) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Log.i("response", "operation started");
        try {
//            imgUrlList = new ArrayList<>();
//            website_url = new ArrayList<>();

            for (int i = 0; i < search.claims.size(); i++) {
                if (isCancelled()) {
                    break;
                }
                long connection_time_start = System.currentTimeMillis();
                Document document = Jsoup.connect(search.claims.get(i).claimReview.get(0).url).ignoreHttpErrors(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36")
                        .cookie("auth", "token").timeout(30_000)
                        .get();
                long connection_time_end = System.currentTimeMillis() - connection_time_start;

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
//                    website_url.add(search.claims.get(i).claimReview.get(0).url);
                }
                publishProgress();
            }
            Log.i("response", "Operation took " + (System.currentTimeMillis() - start) / 1000 + " seconds");

        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Some Strange Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return null;
    }

    //If connection takes too long displays a toast
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

//        if (connection_time_end > 5000) {
//            Toast.makeText(getApplicationContext(), "This is taking longer than expected.\nThis usually happens due to network problems.\n If this continues try changing settings.", Toast.LENGTH_LONG).show();
//        }
    }

    //This executes after background thread finishes its task
    @Override
    protected void onPostExecute(Void aVoid) {
        try {

//            initRecyclerView();
//            progressBar.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "Search Finished.", Toast.LENGTH_LONG).show();
//            if (mIdlingResource != null) {
//                mIdlingResource.setIdleState(true);
//            }
            super.onPostExecute(aVoid);


        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Your search did not match any claims", Toast.LENGTH_SHORT).show();
        }
    }

}
