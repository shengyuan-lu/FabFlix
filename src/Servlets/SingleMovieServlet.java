package Servlets;

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
import Helpers.DatabaseHandler;

// Declaring a WebServlet called Servlets.SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "Servlets.SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve movie id from url request.
        String movieId = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting movie id: " + movieId);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            DatabaseHandler singleMovieDBHandler = new DatabaseHandler(dataSource);

            String singleMovieInfoQuery = "SELECT * FROM movies AS m \n" +
                    "JOIN ratings AS r ON r.movieId = m.id \n" +
                    "WHERE m.id = ?";
            // There is going to be only one row in the query result
            HashMap<String, String> singleMovieInfo = singleMovieDBHandler.executeQuery(singleMovieInfoQuery, movieId).get(0);

            JsonObject singleMovieObj = new JsonObject();

            singleMovieObj.addProperty("movieTitle", singleMovieInfo.get("title"));
            singleMovieObj.addProperty("movieYear", singleMovieInfo.get("year"));
            singleMovieObj.addProperty("movieDirector", singleMovieInfo.get("director"));
            singleMovieObj.addProperty("movieRating", singleMovieInfo.get("rating"));

            String singleMovieGenresQuery = "SELECT g.name AS 'genreName' FROM movies AS m \n" +
                    "JOIN genres_in_movies AS gim ON gim.movieId = m.id \n" +
                    "JOIN genres as g ON g.id = gim.genreId \n" +
                    "WHERE m.id = ?\n" +
                    "ORDER BY genreName\n";

            List<HashMap<String, String>> singleMovieGenres = singleMovieDBHandler.executeQuery(singleMovieGenresQuery, movieId);

            JsonArray singleMovieGenresArr = new JsonArray();
            for (HashMap<String, String> genre : singleMovieGenres) {
                singleMovieGenresArr.add(genre.get("genreName"));
            }

            singleMovieObj.add("movieGenres", singleMovieGenresArr);


            String singleMovieStarsQuery = "SELECT s.name AS name, s.id AS id FROM stars AS s, stars_in_movies AS sm \n" +
                    "WHERE s.id = sm.starId AND sm.movieId=?\n" +
                    "ORDER BY (SELECT COUNT(*) FROM stars_in_movies AS sm2 WHERE sm2.starId = s.id) DESC, s.name \n" +
                    "LIMIT 3";

            List<HashMap<String, String>> singleMovieStars = singleMovieDBHandler.executeQuery(singleMovieStarsQuery, movieId);

            JsonArray singleMovieStarsArr = new JsonArray();

            for (HashMap<String, String> star : singleMovieStars) {
                JsonObject starObj = new JsonObject();
                starObj.addProperty("starId", star.get("id"));
                starObj.addProperty("starName", star.get("name"));

                singleMovieStarsArr.add(starObj);
            }

            singleMovieObj.add("movieStars", singleMovieStarsArr);

            // Write JSON string to output
            out.write(singleMovieObj.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }

}
