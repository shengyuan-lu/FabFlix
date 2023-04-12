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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {

        // Initialize the DataSource
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

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();
            Statement statement_genre = conn.createStatement();
            Statement statement_star = conn.createStatement();

            String movieQuery = "SELECT * FROM movies JOIN ratings r ON movies.id = r.movieId\n" +
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20";

            // Perform the movieQuery
            ResultSet topTwentyMovies = statement.executeQuery(movieQuery);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of topTwentyMovies
            while (topTwentyMovies.next()) {

                String movie_id = topTwentyMovies.getString("id");
                String movie_title = topTwentyMovies.getString("title");
                String movie_year = topTwentyMovies.getString("year");
                String movie_director = topTwentyMovies.getString("director");
                String movie_rating = topTwentyMovies.getString("rating");

                String movieGenreQuery = "SELECT genres.name FROM genres JOIN genres_in_movies gim on genres.id = gim.genreId\n" +
                        "WHERE gim.movieId = '" + movie_id +"'\n" +
                        "LIMIT 3";


                ResultSet genres = statement_genre.executeQuery(movieGenreQuery);

                JsonArray movie_generes = new JsonArray();

                while (genres.next()) {
                    movie_generes.add(genres.getString("name"));
                }

                genres.close();

                String movieStarQuery = "SELECT stars.name FROM stars JOIN stars_in_movies sim on stars.id = sim.starId\n" +
                        "WHERE sim.movieId = '" + movie_id +"'\n" +
                        "LIMIT 3";

                ResultSet stars = statement_star.executeQuery(movieStarQuery);

                JsonArray movie_stars = new JsonArray();

                while (stars.next()) {
                    movie_stars.add(stars.getString("name"));
                }

                stars.close();

                // Create a JsonObject based on the data we retrieve from topTwentyMovies
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movie_generes);
                jsonObject.add("movie_stars", movie_stars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }

            topTwentyMovies.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

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

            // Always remember to close db connection after usage. Here it's done by try-with-resources

            out.close();
        }
    }


}

