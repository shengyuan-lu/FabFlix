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
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DatabaseHandler starDBH = new DatabaseHandler(dataSource);

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {

            /*
            name;
            year of birth (N/A if not available);
            all movies (hyperlinked) in which the star acted.
             */

            // Construct a query with parameter represented by "?"
            String starQuery = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

            List<HashMap<String, String>> singleStar = starDBH.executeQuery(starQuery, id);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of singleStar
            for (HashMap<String, String> ss : singleStar) {

                String starId = ss.get("starId");
                String starName = ss.get("name");
                String starDob = ss.get("birthYear");

                String movieForStarQuery = "SELECT m.id, m.title FROM stars_in_movies\n" +
                        "    JOIN stars s ON stars_in_movies.starId = s.id\n" +
                        "    JOIN movies m ON stars_in_movies.movieId = m.id\n" +
                        "WHERE s.id = ?";

                DatabaseHandler moviesForStarDBH = new DatabaseHandler(dataSource);

                List<HashMap<String, String>> movies = moviesForStarDBH.executeQuery(movieForStarQuery, starId);

                moviesForStarDBH.close();

                JsonArray moviesArray = new JsonArray();

                for (HashMap<String, String> movie : movies) {

                    String movieId = movie.get("id");
                    String movieTitle = movie.get("title");

                    JsonObject movieObj = new JsonObject();
                    movieObj.addProperty("movie_id", movieId);
                    movieObj.addProperty("movie_title", movieTitle);

                    moviesArray.add(movieObj);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.add("movies", moviesArray);

                jsonArray.add(jsonObject);
            }

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

            // Close resources
            starDBH.close();

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
