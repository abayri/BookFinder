package com.example.android.bookfinder.Model;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String mUrl;

    public static final String LOG_TAG = BookLoader.class.getName();

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Commencing onStartLoading().");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i(LOG_TAG, "Commencing loadInBackground().");

        if (mUrl == null) {
            return null;
        }

        return QueryUtils.extractBooks(mUrl);
    }
}
