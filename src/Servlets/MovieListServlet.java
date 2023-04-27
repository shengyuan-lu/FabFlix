package Servlets;

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
import Helpers.DatabaseHandler;


@WebServlet(name = "Servlets.MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DatabaseHandler movieListDBHandler = new DatabaseHandler(dataSource);

        response.setContentType("application/json"); // Response type

        // Output stream
        PrintWriter out = response.getWriter();

        // 5 possible query strings
        // title (string like pattern needed)
        // genre_id
        // star_name (string like pattern needed)
        // director_name (string like pattern needed)
        // year

        //
        // Require movie table only
        //

        // title is only search
        String title = "%";

        if (!(request.getParameter("title") == null)) {
            title = "%" + request.getParameter("title") + "%";
        }

        // alphabet is only browse
        String alphabet = null;

        if (!(request.getParameter("alphabet") == null)) {
            alphabet = request.getParameter("alphabet") + "%";

            if (alphabet.equals("*%")) {
                alphabet = "^[^A-Za-z0-9]";
            }
        }


        // director_name is only search
        String director_name = "%";

        if (!(request.getParameter("director_name") == null)) {
            director_name = "%" + request.getParameter("director_name") + "%";
        }

        // year is only search
        String year = request.getParameter("year");

        //
        // Require JOIN another table
        //

        // genre_id is only browse
        String genre_id = null;

        if (!(request.getParameter("genre_id") == null)) {
            genre_id = "%" + request.getParameter("genre_id") + "%";
        }

        // star_name is only search
        String star_name = "%";

        if (!(request.getParameter("star_name") == null)) {
            star_name = "%" + request.getParameter("star_name") + "%";
        }

        try {

            String movieQuery;

            List<HashMap<String, String>> topTwentyMovies;

            if (genre_id != null && alphabet == null) {

                // Handle genre_id and alphabet
                // The only query string that will appear alone

                movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                        "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                        "JOIN ratings r ON movies.id = r.movieId\n" +
                        "WHERE gim.genreId LIKE ?\n" +
                        "LIMIT 100";

                topTwentyMovies = movieListDBHandler.executeQuery(movieQuery, genre_id);

            } else if (alphabet != null && genre_id == null) {

                String whereClause;

                if (alphabet.equals("^[^A-Za-z0-9]")) {
                    whereClause = "WHERE title regexp ? \n";
                } else {
                    whereClause = "WHERE LOWER(title) LIKE LOWER(?) \n";
                }

                movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                        "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                        "JOIN ratings r ON movies.id = r.movieId\n" +
                        whereClause +
                        "LIMIT 100";

                topTwentyMovies = movieListDBHandler.executeQuery(movieQuery, alphabet);

            } else {

                // Handle title, director_name, year, star_name
                String yearClause;

                if (year != null) {
                    yearClause = "AND year = " + year + "\n";
                } else {
                    yearClause = "";
                }

                movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                        "JOIN ratings r ON movies.id = r.movieId\n" +
                        "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                        "JOIN stars_in_movies sim ON movies.id = sim.movieId\n" +
                        "JOIN stars ON stars.id = sim.starId\n" +
                        "WHERE title LIKE ?\n" +
                        "AND director LIKE ?\n" +
                        yearClause +
                        "AND stars.name LIKE ?\n" +
                        "GROUP BY movies.id, title, year, director, price, rating\n" +
                        "LIMIT 100";

                topTwentyMovies = movieListDBHandler.executeQuery(movieQuery, title, director_name, star_name);
            }

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of topTwentyMovies
            for (HashMap<String, String> movie : topTwentyMovies) {

                String movie_id = movie.get("id");
                String movie_title = movie.get("title");
                String movie_year = movie.get("year");
                String movie_director = movie.get("director");
                String movie_rating = movie.get("rating");

                String movieGenreQuery = "SELECT genres.id, genres.name FROM genres \n" +
                        "JOIN genres_in_movies gim ON genres.id = gim.genreId\n" +
                        "WHERE gim.movieId = ?\n" +
                        "LIMIT 3";

                List<HashMap<String, String>> genres = movieListDBHandler.executeQuery(movieGenreQuery, movie_id);

                JsonArray movie_genres = new JsonArray();

                for (HashMap<String, String> genre : genres) {
                    JsonObject gr = new JsonObject();

                    gr.addProperty("id", genre.get("id"));
                    gr.addProperty("name", genre.get("name"));

                    movie_genres.add(gr);
                }

                String movieStarQuery = "SELECT stars.id, stars.name FROM stars\n" +
                        "JOIN stars_in_movies sim ON stars.id = sim.starId\n" +
                        "WHERE sim.movieId = ?\n" +
                        "LIMIT 3";

                List<HashMap<String, String>> stars = movieListDBHandler.executeQuery(movieStarQuery, movie_id);

                JsonArray movie_stars = new JsonArray();

                for (HashMap<String, String> star : stars) {
                    JsonObject st = new JsonObject();

                    st.addProperty("id", star.get("id"));
                    st.addProperty("name", star.get("name"));

                    movie_stars.add(st);
                }

                // Create a JsonObject based on the data we retrieve from topTwentyMovies
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", Integer.parseInt(movie_year));
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movie_genres);
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

