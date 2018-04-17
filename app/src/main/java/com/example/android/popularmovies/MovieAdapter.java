package com.example.android.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jonathanbarrera on 4/12/18.
 * Exposes a list of Movie objects to the recycler view
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/w500/";

    private List<Movie> mMovieData;
    private final MovieAdapterOnClickHandler mClickHandler;

    // The interface that receives onClick messages
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        // Inflate the list item view
        View view = layoutInflater.inflate(R.layout.movie_list_item, viewGroup, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = mMovieData.get(position);
        String imageLinkPath = currentMovie.getImageLink();
        Context context = holder.mMoviePosterIV.getContext();

        // Use Picasso to get the movie poster Image
        Picasso.with(context)
                .load(BASE_URL + imageLinkPath)
                .into(holder.mMoviePosterIV);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    /**
     * Cache of the children views for a Movie list item
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Declare a public variable for the imageview
        public final ImageView mMoviePosterIV;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            // find the image view in the movie_list_item.xml
            mMoviePosterIV = itemView.findViewById(R.id.movie_poster_image_view);
            itemView.setOnClickListener(this);
        }

        // Override onClick to get the movie clicked on, and to call the mClickHandler.onClick
        // method using the movie object as a parameter.
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    public void setMovieData(List<Movie> moviesList) {
        mMovieData = moviesList;
        notifyDataSetChanged();
    }
}
