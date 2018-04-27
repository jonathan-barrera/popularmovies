package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Review;

import java.util.ArrayList;

/**
 * Created by jonathanbarrera on 4/27/18.
 * Adapts a list of Review objects to a list view
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    // Constructor
    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data for this position
        Review review = getItem(position);
        // Create view if it doesn't exist
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item,
                    parent, false);
        }
        // Get references to the text views
        TextView contentTextView = convertView.findViewById(R.id.review_content_text_view);
        TextView authorTextView = convertView.findViewById(R.id.review_author_text_view);

        // Set the information to the text views
        contentTextView.setText(review.getReviewContent());
        authorTextView.setText(review.getReviewAuthor());

        return convertView;
    }
}
