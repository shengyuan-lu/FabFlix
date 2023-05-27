package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static edu.uci.ics.fabflixmobile.data.Constants.baseURL;

public class MovieListActivity extends AppCompatActivity {

    private final String TAG = "MovieList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final ArrayList<Movie> movies = new ArrayList<>();
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // TODO: transition to single movie page
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public void getMovies() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/movies",
                response -> {
                    Log.d(TAG, response);
                    try {
                        JSONObject responseJsonObj = new JSONObject(response); // Convert response string to a JSON object

                        if (responseJsonObj.get("status").equals("success")) {
                            finish(); // Complete and destroy login activity once successful

                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
                            // activate the list page.
                            startActivity(MovieListPage);
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "Json parse error");
                    }
                },
                error -> {
                    // error
                    Log.d(TAG, error.toString());
                });
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}