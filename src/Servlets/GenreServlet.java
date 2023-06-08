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

@WebServlet(name = "GenreServlet", urlPatterns = "/api/genre")
public class GenreServlet extends HttpServlet {

    // Create a dataSource
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDBSlave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DatabaseHandler genreDBH = new DatabaseHandler(dataSource);

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {

            String genreQuery = "SELECT id, name FROM genres\n" +
                    "ORDER BY name ASC\n"; // Customers should have a list of all hyperlinked genres sorted
                                           // alphabetically.

            List<HashMap<String, String>> genreResult = genreDBH.executeQuery(genreQuery);

            JsonArray genreArray = new JsonArray();

            // Iterate through each row of singleStar
            for (HashMap<String, String> g : genreResult) {

                JsonObject singleGenre = new JsonObject();

                singleGenre.addProperty("genre_id", g.get("id"));
                singleGenre.addProperty("genre_name", g.get("name"));

                genreArray.add(singleGenre);
            }

            // Write JSON string to output
            out.write(genreArray.toString());

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
