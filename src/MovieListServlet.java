import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movies"

/*
In Project 1, the Movie list Page shows the top 20 rated movies, sorted by the rating. You don't need to show all the movies. Each movie needs to contain the following information:

title;
year;
director;
first three genres (order does not matter) ;
first three stars (order does not matter);
rating.

 */

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")

public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DatabaseHandler movieListDBHandler = new DatabaseHandler(dataSource);

        response.setContentType("application/json"); // Response type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

            String movieQuery = "SELECT * FROM movies JOIN ratings r ON movies.id = r.movieId\n" +
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20";

            // Perform the movieQuery
            List<HashMap<String, String>> topTwentyMovies = movieListDBHandler.executeQuery(movieQuery);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of topTwentyMovies
            for (HashMap<String, String> movie : topTwentyMovies) {

                String movie_id = movie.get("id");
                String movie_title = movie.get("title");
                String movie_year = movie.get("year");
                String movie_director = movie.get("director");
                String movie_rating = movie.get("rating");

                String movieGenreQuery = "SELECT genres.name FROM genres JOIN genres_in_movies gim ON genres.id = gim.genreId\n" +
                        "WHERE gim.movieId = '" + movie_id + "'\n" +
                        "LIMIT 3";

                List<HashMap<String, String>> genres = movieListDBHandler.executeQuery(movieGenreQuery);

                JsonArray movie_generes = new JsonArray();

                for (HashMap<String, String> genre : genres) {
                    movie_generes.add(genre.get("name"));
                }

                String movieStarQuery = "SELECT stars.name FROM stars JOIN stars_in_movies sim ON stars.id = sim.starId\n" +
                        "WHERE sim.movieId = '" + movie_id + "'\n" +
                        "LIMIT 3";

                List<HashMap<String, String>> stars = movieListDBHandler.executeQuery(movieStarQuery);

                JsonArray movie_stars = new JsonArray();

                for (HashMap<String, String> star : stars) {
                    movie_stars.add(star.get("name"));
                }

                // Create a JsonObject based on the data we retrieve from topTwentyMovies
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", Integer.parseInt(movie_year));
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movie_generes);
                jsonObject.add("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", Double.parseDouble(movie_rating));

                jsonArray.add(jsonObject);

            }

            // Log to localhost log
            request.getServletContext().log("api/movies getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

                // Write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);

        } finally {
            out.close();
        }
    }
}

