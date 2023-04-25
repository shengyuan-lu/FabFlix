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

            String singleMovieInfoQuery = "select * from movies as m " +
                    "join ratings as r on r.movieId = m.id " +
                    "where m.id = ?";
            // There is going to be only one row in the query result
            HashMap<String, String> singleMovieInfo = singleMovieDBHandler.executeQuery(singleMovieInfoQuery, movieId).get(0);

            JsonObject singleMovieObj = new JsonObject();

            singleMovieObj.addProperty("movieTitle", singleMovieInfo.get("title"));
            singleMovieObj.addProperty("movieYear", singleMovieInfo.get("year"));
            singleMovieObj.addProperty("movieDirector", singleMovieInfo.get("director"));
            singleMovieObj.addProperty("movieRating", singleMovieInfo.get("rating"));

            String singleMovieGenresQuery = "select g.name as 'genreName' from movies as m " +
                    "join genres_in_movies as gim on gim.movieId = m.id " +
                    "join genres as g on g.id = gim.genreId " +
                    "where m.id = ?";
            List<HashMap<String, String>> singleMovieGenres = singleMovieDBHandler.executeQuery(singleMovieGenresQuery, movieId);

            JsonArray singleMovieGenresArr = new JsonArray();
            for (HashMap<String, String> genre : singleMovieGenres) {
                singleMovieGenresArr.add(genre.get("genreName"));
            }

            singleMovieObj.add("movieGenres", singleMovieGenresArr);


            String singleMovieStarsQuery = "select s.id as 'starId', s.name as 'starName' from movies as m " +
                    "join stars_in_movies as sim on sim.movieId = m.id " +
                    "join stars as s on s.id = sim.starId " +
                    "where m.id = ?";
            List<HashMap<String, String>> singleMovieStars = singleMovieDBHandler.executeQuery(singleMovieStarsQuery, movieId);

            JsonArray singleMovieStarsArr = new JsonArray();

            for (HashMap<String, String> star : singleMovieStars) {
                JsonObject starObj = new JsonObject();
                starObj.addProperty("starId", star.get("starId"));
                starObj.addProperty("starName", star.get("starName"));

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
