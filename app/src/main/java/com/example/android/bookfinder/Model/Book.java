package com.example.android.bookfinder.Model;

public class Book {
    // Book class contains title, authors and the Google Books url
    private String mBookTitle;
    private String mAuthors;
    private String mUrl;

    public Book(String bookTitle, String authors, String url) {
        mBookTitle = bookTitle;
        mAuthors = authors;
        mUrl = url;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getUrl() {
        return mUrl;
    }
}
