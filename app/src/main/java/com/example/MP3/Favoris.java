package com.example.MP3;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Favoris extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noFavoritesTextView;
    ArrayList<AudioModel> favoritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);

        recyclerView = findViewById(R.id.view_favorites);
        noFavoritesTextView = findViewById(R.id.no_favorites);

        // Retrieve the favorites from the database using the DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getFavorites();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") AudioModel favoriteSong = new AudioModel(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION))
            );
            favoritesList.add(favoriteSong);

        }
        cursor.close();

        if (favoritesList.size() == 0) {
            noFavoritesTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(favoritesList, getApplicationContext()));
        }

    }
}