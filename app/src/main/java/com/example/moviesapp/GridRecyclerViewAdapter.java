package com.example.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GridRecyclerViewAdapter";
    private static ArrayList<Movie> mMovies = new ArrayList<>();
    private String mMovieTittle = "";
    private String mMovieUrl = "";
    private Context mContext;
    private OnMoviesListener mOnMoviesListener;

    public GridRecyclerViewAdapter(Context context, OnMoviesListener onMoviesListener, ArrayList<Movie> jsonMovieData) {
        mMovies = jsonMovieData;
        mContext = context;
        mOnMoviesListener = onMoviesListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int resource = R.layout.layout_item_movie;
        boolean attachRoot = false;
        View view = LayoutInflater.from(context).inflate(resource, parent, attachRoot);
        return new ViewHolder(view, mOnMoviesListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "OnViewHolder method called");
        // position reffers to the position in the list

        Movie currentMovie = mMovies.get(position);
        mMovieTittle = currentMovie.getmTitle();
        mMovieUrl = currentMovie.getmFullPosterUrl();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Glide.with(mContext)
                .load(mMovieUrl)
                .apply(requestOptions)
                .into(viewHolder.movieImage);

        // set correct text to the tittle of the movie
        viewHolder.movieTittle.setText(mMovieTittle);
    }

    @Override
    public int getItemCount() {
        // tells the recyclerview adapter class how many items are there going to be
        return mMovies.size();
    }

    public interface OnMoviesListener {
        void onMovieCLick(Movie currentMovie);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieImage;
        TextView movieTittle;
        OnMoviesListener onMoviesListener;

        public ViewHolder(View itemView, OnMoviesListener moviesListener) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.iv_movie_grid);
            movieTittle = itemView.findViewById(R.id.tv_movie_tittle);
            onMoviesListener = moviesListener;
            //attach onclick listener to the entire view holder
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = mMovies.get(adapterPosition);
            onMoviesListener.onMovieCLick(currentMovie);

        }
    }
}
