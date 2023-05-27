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
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        MovieListActivity currentActivity = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("query", (String) searchView.getQuery().toString());
                String temp_query = searchView.getQuery().toString();
                //update movie list
                currentActivity.getMovies(new ArrayList<>(),temp_query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return false;}
            });

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        ArrayList<Movie> movies = new ArrayList<>();
        getMovies(movies,"movie"); // Fill movies list



    }

    public void getMovies(ArrayList<Movie> movies, String query) {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/movies?ft=true&title="+query+"&sort=rating&offset=0&limit=10&title_order=asc&rating_order=desc",
                response -> {
                    Log.d(TAG, response);
                    try {
                        JSONArray responseJsonArr = new JSONArray(response); // Convert response string to a JSON object
                        adapter = new MovieListViewAdapter(this, movies);
                        adapter.notifyDataSetChanged();
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            // TODO: transition to single movie page
//            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        });

                        for (int i = 0; i < responseJsonArr.length(); ++i) {
                            JSONObject movieObj = (JSONObject) responseJsonArr.get(i);// Get movie info object
                            String title = (String) movieObj.get("movie_title");
                            int year = (int) movieObj.get("movie_year");
                            String director = (String) movieObj.get("movie_director");
                            JSONArray genres = (JSONArray) movieObj.get("movie_genres");
                            JSONArray stars = (JSONArray) movieObj.get("movie_stars");

                            movies.add(new Movie(title, year, director, genres, stars)); // Add this movie to movies list
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