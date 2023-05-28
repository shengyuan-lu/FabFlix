package edu.uci.ics.fabflixmobile.ui.singlemovie;

import static edu.uci.ics.fabflixmobile.data.Constants.baseURL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivitySingleMovieBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainActivity;

public class SingleMovieActivity extends AppCompatActivity {

    private final String TAG = "SingleMovie";

    private TextView titleTextView;
    private TextView yearTextView;
    private TextView directorTextView;
    private TextView genresTextView;
    private TextView starsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivitySingleMovieBinding binding = ActivitySingleMovieBinding.inflate(getLayoutInflater());

        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        titleTextView = binding.title;
        yearTextView = binding.year;
        directorTextView = binding.director;
        genresTextView = binding.genres;
        starsTextView = binding.stars;

        String movieId = getIntent().getStringExtra("movieId");

        if (movieId == null) {
            Log.d(TAG, "movieId is null!");
            movieId = "";
        } else {
            Log.d(TAG, "movieId is " + movieId);
        }

        updatefields(movieId);
    }

    @SuppressLint("SetTextI18n")
    public void updatefields(String movieId) {

        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // request type is POST
        final StringRequest singleMovieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + movieId,
                response -> {

                    Log.d(TAG, response);

                    try {

                        JSONObject movieObj = new JSONObject(response); // Convert response string to a JSON object

                        String title = (String) movieObj.get("movieTitle");
                        String year = (String) movieObj.get("movieYear");
                        String director = (String) movieObj.get("movieDirector");
                        JSONArray genres = (JSONArray) movieObj.get("movieGenres");
                        JSONArray stars = (JSONArray) movieObj.get("movieStars");

                        titleTextView.setText(title);
                        yearTextView.setText("Year: " + year);
                        directorTextView.setText("Director: " + director);

                        List<String> genreStrings = new ArrayList<>();

                        for (int i = 0; i < genres.length(); ++i) {
                            try {
                                genreStrings.add((String) genres.get(i));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        String genresString = String.join(", ", genreStrings);

                        genresTextView.setText("Genres: " + genresString);

                        List<String> starStrings = new ArrayList<>();

                        for (int i = 0; i < stars.length(); ++i) {
                            try {
                                JSONObject star = (JSONObject) stars.get(i);
                                starStrings.add((String) star.get("starName"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        String starsString = String.join(", ", starStrings);

                        starsTextView.setText("Stars: " + starsString);

                    } catch (JSONException e) {
                        Log.d(TAG, "Json parse error");
                    }
                },
                error -> {
                    // error
                    Log.d(TAG, error.toString());

                }) {

        };

        // important: queue.add is where the login request is actually sent
        queue.add(singleMovieRequest);

    }
}
