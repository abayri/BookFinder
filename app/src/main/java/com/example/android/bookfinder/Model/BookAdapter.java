package com.example.android.bookfinder.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.bookfinder.R;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    public static final String LOG_TAG = BookAdapter.class.getName();

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitle.setText(currentBook.getBookTitle());

        TextView bookAuthors = (TextView) listItemView.findViewById(R.id.book_authors);
        bookAuthors.setText(currentBook.getAuthors());

        return listItemView;
    }
}

