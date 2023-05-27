package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.SearchManager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import android.os.Bundle;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static edu.uci.ics.fabflixmobile.data.Constants.baseURL;

public class MovieListActivity extends AppCompatActivity {

    private final String TAG = "MovieList";

    MovieListViewAdapter adapter = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MovieListActivity currentActivity = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String temp_query = searchView.getQuery().toString();

                Log.d(TAG, temp_query);

                currentActivity.getMovies(new ArrayList<>(), temp_query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return false;
            }
        });

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movielist);

        ArrayList<Movie> movies = new ArrayList<>();

        String movieTitle = getIntent().getStringExtra("movieTitle");

        if (movieTitle == null) {
            Log.d(TAG, "movieTitle is null!");
            movieTitle = "";
        }

        getMovies(movies, movieTitle);
    }


    public void getMovies(ArrayList<Movie> movies, String query) {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String apiURL;

        if (query.equals("")) {
            apiURL = "/api/movies?sort=rating&offset=0&limit=10&title_order=asc&rating_order=desc";
        } else {
            apiURL = "/api/movies?ft=true&title="+query+"&sort=rating&offset=0&limit=10&title_order=asc&rating_order=desc";
        }

        // request type is POST
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL,
                response -> {
                    Log.d(TAG, response);
                    try {
                        JSONArray responseJsonArr = new JSONArray(response); // Convert response string to a JSON object

                        adapter = new MovieListViewAdapter(this, movies);

                        adapter.notifyDataSetChanged();

                        ListView listView = findViewById(R.id.list);

                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view, position, id) -> {

                            Log.d(TAG, "Go to single movie");

                            Movie movie = movies.get(position);

                            // initialize the activity(page)/destination
                            Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);

                            SingleMoviePage.putExtra("movieId", movie.getId());

                            // activate the list page.
                            startActivity(SingleMoviePage);
                        });

                        for (int i = 0; i < responseJsonArr.length(); ++i) {

                            JSONObject movieObj = (JSONObject) responseJsonArr.get(i);// Get movie info object

                            String title = (String) movieObj.get("movie_title");
                            String id = (String) movieObj.get("movie_id");
                            int year = (int) movieObj.get("movie_year");
                            String director = (String) movieObj.get("movie_director");
                            JSONArray genres = (JSONArray) movieObj.get("movie_genres");
                            JSONArray stars = (JSONArray) movieObj.get("movie_stars");

                            movies.add(new Movie(title, id, year, director, genres, stars)); // Add this movie to movies list
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
        queue.add(movieListRequest);
    }
}