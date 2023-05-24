package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import Helpers.DatabaseHandler;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "MovieSuggestionServlet", urlPatterns = "/api/movie-suggestion")
public class MovieSuggestion extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {

        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    /*
     * populate movieMap
     * Key is movieID. Value is movieTitle.
     */

    // public static HashMap<String, String> movieMap = new HashMap<>();

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "movieID": "101" } },
     * 	{ "value": "Supergirl", "data": { "movieID": "113" } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DatabaseHandler suggestionDBHandler = new DatabaseHandler(dataSource);

        response.setContentType("application/json"); // Response type

        PrintWriter out = response.getWriter();

        try {
            // create the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String movieTitle = "%" + request.getParameter("query") + "%"; // TODO: Full Text Search
            // String movieTitle = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (movieTitle == null || movieTitle.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String query = "SELECT id, title FROM movies WHERE title LIKE ?"; // TODO: Full Text Search
            // String query = "SELECT id, title, year FROM movies WHERE MATCH (title) AGAINST ( ? IN BOOLEAN MODE) LIMIT 10;";

            // search on moviedb and add the results to JSON Array
            List<HashMap<String, String>> movies = suggestionDBHandler.executeQuery(query, movieTitle);

            for (HashMap<String, String> movie : movies) {
                jsonArray.add(generateMovieJsonObject(movie.get("id"), movie.get("title")));
            }

            out.write(jsonArray.toString());

        } catch (Exception e) {

            System.out.println(e);
            response.sendError(500, e.getMessage());

        } finally {
            out.close();
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "movieID": 11 }
     * }
     *
     */
    private static JsonObject generateMovieJsonObject(String movieID, String movieTitle) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);

        return jsonObject;
    }


}