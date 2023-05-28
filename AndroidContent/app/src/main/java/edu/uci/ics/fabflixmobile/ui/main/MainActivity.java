package edu.uci.ics.fabflixmobile.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import edu.uci.ics.fabflixmobile.databinding.ActivityMainBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Main";

    private EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        search = binding.searchTextbox;

        final Button searchButton = binding.searchButton;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> executeSearch());
    }

    @SuppressLint("SetTextI18n")
    public void executeSearch() {

        Log.d(TAG, "Execute Search");

        // initialize the activity(page)/destination
        Intent MovieListPage = new Intent(MainActivity.this, MovieListActivity.class);

        MovieListPage.putExtra("movieTitle", search.getText().toString());

        // activate the list page.
        startActivity(MovieListPage);

    }
}