/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.bookfinder.UI;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bookfinder.Model.Book;
import com.example.android.bookfinder.Model.BookAdapter;
import com.example.android.bookfinder.Model.BookLoader;
import com.example.android.bookfinder.R;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements android.app.LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = BookActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;

    // URL to query the Google Books API with a maximum number of 10 results
    private String bookUrl = "https://www.googleapis.com/books/v1/volumes?q=";
    private String maxResults = "&maxResults=10";

    // Private declarations for the adapter and other views in the BookActivity layout
    private BookAdapter mAdapter;
    private ImageView mMainImage;
    private TextView mTitleMessage;
    private EditText mEditText;
    private Button mSearchButton;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;
    private ConnectivityManager mConnection;

    // True or false statement to see if network is connected
    // This will check for connectivity every time the search button is clicked
    private boolean isConnected() {
        mConnection = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = mConnection.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "BookActivity onCreate() is called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        // Find reference to the main book image
        mMainImage = (ImageView) findViewById(R.id.main_image);

        // Find reference to the title TextView
        mTitleMessage = (TextView) findViewById(R.id.title_text);

        // Find reference to the EditText
        mEditText = (EditText) findViewById(R.id.user_query);

        // Find reference to the search button
        mSearchButton = (Button) findViewById(R.id.search_query);

        // Find reference to the empty TextView
        mEmptyTextView = (TextView) findViewById(R.id.empty_text);

        // Find reference to the progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.GONE);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        earthquakeListView.setEmptyView(mEmptyTextView);

        // Creates a new adapter taking in the books list as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long d) {
                Book currentBook = mAdapter.getItem(position);
                Intent browser = new Intent(Intent.ACTION_VIEW);
                browser.setData(Uri.parse(currentBook.getUrl()));
                startActivity(browser);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookUrl = bookUrl + mEditText.getText().toString().toLowerCase() + maxResults;

                if (isConnected()) {
                    Log.i(LOG_TAG, "Calling initLoader()...");
                    mMainImage.setVisibility(View.GONE);
                    mTitleMessage.setVisibility(View.GONE);
                    mEditText.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().initLoader(BOOK_LOADER_ID, null, BookActivity.this).forceLoad();
                } else {
                    mTitleMessage.setVisibility(View.GONE);
                    mEditText.setVisibility(View.GONE);
                    mSearchButton.setVisibility(View.GONE);
                    mEmptyTextView.setText(R.string.no_connection);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "Calling onCreateLoader()...");
        return new BookLoader(BookActivity.this, bookUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.i(LOG_TAG, "Calling onLoadFinished()...");
        mProgressBar.setVisibility(View.GONE);
        mMainImage.setVisibility(View.GONE);
        mTitleMessage.setVisibility(View.GONE);
        mEditText.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.GONE);
        mEmptyTextView.setText(R.string.empty_books);
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "Calling onLoaderReset()...");
        mAdapter.clear();
    }

    public void onBackPressed() {
        Intent intent = new Intent(BookActivity.this, BookActivity.class);
        startActivity(intent);
    }
}
