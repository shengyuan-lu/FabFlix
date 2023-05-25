package Servlets;

import Helpers.DatabaseHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
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

        //
        // Search
        //

        // title
        String title = "%";

        if (request.getParameter("title") != null) {
            title = "%" + request.getParameter("title") + "%";
        }

        // director_name
        String director_name = "%";

        if (request.getParameter("director_name") != null) {
            director_name = "%" + request.getParameter("director_name") + "%";
        }

        // year
        String year = request.getParameter("year");

        // star_name
        String star_name = "%";

        if (request.getParameter("star_name") != null) {
            star_name = "%" + request.getParameter("star_name") + "%";
        }


        //
        // Browse
        //

        // genre_id
        String genre_id = request.getParameter("genre_id");

        // alphabet
        String alphabet = null;

        if (request.getParameter("alphabet") != null) {
            alphabet = request.getParameter("alphabet") + "%";

            if (alphabet.equals("*%")) {
                alphabet = "^[^A-Za-z0-9]";
            }
        }

        //
        // Both Search and Browse
        //

        // paganization
        int offset = 0;

        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }

        int limit = 10;

        if (request.getParameter("limit") != null) {
            limit = Integer.parseInt(request.getParameter("limit"));
        }

        // sorting
        String sort = "rating";

        if (!request.getParameter("sort").equals("rating")) {
            sort = "title";
        }

        String rating_order = "desc";

        if (!request.getParameter("rating_order").equals("desc")) {
            rating_order = "asc";
        }

        String title_order = "asc";

        if (!request.getParameter("title_order").equals("asc")) {
            title_order = "desc";
        }

        try {

            String movieQuery;

            List<HashMap<String, String>> movieList;

            String sortClause;

            if (sort.equals("rating")) {
                sortClause = String.format("ORDER BY rating %s, title %s \n", rating_order.toUpperCase(), title_order.toUpperCase());
            } else {
                sortClause = String.format("ORDER BY title %s, rating %s \n", title_order.toUpperCase(), rating_order.toUpperCase());
            }

            String paginationClause = String.format("LIMIT %s OFFSET %s \n", limit, offset);

            if (request.getParameter("ft") != null && request.getParameter("ft").equals("true")) {

                movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                        "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                        "JOIN ratings r ON movies.id = r.movieId\n" +
                        "WHERE MATCH (title) AGAINST ( ? IN BOOLEAN MODE)\n" +
                        "GROUP BY movies.id, title, year, director, price, rating\n" +
                        sortClause +
                        paginationClause;

                movieList = movieListDBHandler.executeQuery(movieQuery, title);

            } else if (genre_id != null && alphabet == null) {

                // Handle browse by genre

                movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                        "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                        "JOIN ratings r ON movies.id = r.movieId\n" +
                        "WHERE gim.genreId = ?\n" +
                        "GROUP BY movies.id, title, year, director, price, rating\n" +
                        sortClause +
                        paginationClause;

                movieList = movieListDBHandler.executeQuery(movieQuery, genre_id);

            } else if (alphabet != null && genre_id == null) {

                // Handle browse by alphabet

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
                        "GROUP BY movies.id, title, year, director, price, rating\n" +
                        sortClause +
                        paginationClause;

                movieList = movieListDBHandler.executeQuery(movieQuery, alphabet);

            } else {

                // Handle title, director_name, year, star_name

                if (year != null) {

                    movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                            "JOIN ratings r ON movies.id = r.movieId\n" +
                            "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                            "JOIN stars_in_movies sim ON movies.id = sim.movieId\n" +
                            "JOIN stars ON stars.id = sim.starId\n" +
                            "WHERE title LIKE ?\n" +
                            "AND director LIKE ?\n" +
                            "AND year = ?\n" +
                            "AND stars.name LIKE ?\n" +
                            "GROUP BY movies.id, title, year, director, price, rating\n" +
                            sortClause +
                            paginationClause;

                    movieList = movieListDBHandler.executeQuery(movieQuery, title, director_name, year, star_name);

                } else {

                    movieQuery = "SELECT movies.id, title, year, director, price, rating FROM movies\n" +
                            "JOIN ratings r ON movies.id = r.movieId\n" +
                            "JOIN genres_in_movies gim ON movies.id = gim.movieId\n" +
                            "JOIN stars_in_movies sim ON movies.id = sim.movieId\n" +
                            "JOIN stars ON stars.id = sim.starId\n" +
                            "WHERE title LIKE ?\n" +
                            "AND director LIKE ?\n" +
                            "AND stars.name LIKE ?\n" +
                            "GROUP BY movies.id, title, year, director, price, rating\n" +
                            sortClause +
                            paginationClause;

                    movieList = movieListDBHandler.executeQuery(movieQuery, title, director_name, star_name);

                }

            }

            JsonArray jsonArray = new JsonArray();

            for (HashMap<String, String> movie : movieList) {

                String movie_id = movie.get("id");
                String movie_title = movie.get("title");
                String movie_year = movie.get("year");
                String movie_director = movie.get("director");
                String movie_rating = movie.get("rating");

                String movieGenreQuery = "SELECT genres.id, genres.name FROM genres \n" +
                        "JOIN genres_in_movies gim ON genres.id = gim.genreId\n" +
                        "WHERE gim.movieId = ?\n" +
                        "ORDER BY genres.name\n" +
                        "LIMIT 3\n";

                List<HashMap<String, String>> genres = movieListDBHandler.executeQuery(movieGenreQuery, movie_id);

                JsonArray movie_genres = new JsonArray();

                for (HashMap<String, String> genre : genres) {
                    JsonObject gr = new JsonObject();

                    gr.addProperty("id", genre.get("id"));
                    gr.addProperty("name", genre.get("name"));

                    movie_genres.add(gr);
                }

                String movieStarQuery = "SELECT s.name AS name, s.id AS id FROM stars AS s, stars_in_movies AS sm \n" +
                        "WHERE s.id = sm.starId AND sm.movieId = ?\n" +
                        "ORDER BY (SELECT COUNT(*) FROM stars_in_movies AS sm2 WHERE sm2.starId = s.id) DESC, s.name \n" +
                        "LIMIT 3\n";

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
