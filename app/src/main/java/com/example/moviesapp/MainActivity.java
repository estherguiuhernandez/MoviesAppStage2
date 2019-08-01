package com.example.moviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.OnMoviesListener {
    private static final String TAG = "MainActivity";
    private static final int NUM_GRID_COLUMS = 3;

    private ArrayList<String> mImageUrl = new ArrayList<>();
    private ArrayList<String> mMovieTittle= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageBitMaps();
        initRecyclerView();
    }

    private void initImageBitMaps(){
        Log.d(TAG,"preparing bit maps");

        mImageUrl.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mMovieTittle.add("Havasu Falls");

        mImageUrl.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mMovieTittle.add("Trondheim");

        mImageUrl.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mMovieTittle.add("Portugal");

        mImageUrl.add("https://i.redd.it/j6myfqglup501.jpg");
        mMovieTittle.add("Rocky Mountain National Park");

        mImageUrl.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mMovieTittle.add("Mahahual");

        mImageUrl.add("https://i.redd.it/k98uzl68eh501.jpg");
        mMovieTittle.add("Frozen Lake");

        mImageUrl.add("https://i.redd.it/glin0nwndo501.jpg");
        mMovieTittle.add("White Sands Desert");

        mImageUrl.add("https://i.redd.it/obx4zydshg601.jpg");
        mMovieTittle.add("Austrailia");

        mImageUrl.add("https://i.imgur.com/ZcLLrkY.jpg");
        mMovieTittle.add("Washington");
    }

    /**
     * This method initializes the recycler view and its adapter
     */
    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView method");
        RecyclerView recyclerView = findViewById(R.id.rv_movies);
        // the scond this refers to the interface
        GridRecyclerViewAdapter gridReyclerViewAdapter =
                new GridRecyclerViewAdapter(MainActivity.this, this, mMovieTittle, mImageUrl );
        // set a GridLayoutManager with NUM_GRID_COLUMS number of columns , horizontal gravity and false
        // value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),NUM_GRID_COLUMS, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerVie
        recyclerView.setAdapter(gridReyclerViewAdapter);
    }


    @Override
    public void onMovieCLick(int position) {
        Log.d(TAG, "onMovieCLick: Clicked");
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }
}

