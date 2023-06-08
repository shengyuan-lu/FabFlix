package Servlets;

import Helpers.DatabaseHandler;
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
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "DashboardAddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class DashboardAddStarServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getServletContext().log("Get in add star servlet.");

        String starName = request.getParameter("starName");
        String starBirthYearString = request.getParameter("starBirthYear");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObj = new JsonObject();

        if (starName.isEmpty()) {
            responseJsonObj.addProperty("status", "failed");
            responseJsonObj.addProperty("message", "Entering a star name is required.");

            // Write JSON string to output
            out.write(responseJsonObj.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            return;
        }

        try {
            DatabaseHandler addStarDBHandler = new DatabaseHandler(dataSource);

            String retrieveMaxStarIdQuery = "select max(id) as max_star_id from stars;";
            String updateStarsQuery = "insert into stars (id, name, birthYear)\n" +
                    "values (?, ?, ?);";

            String maxStarId = addStarDBHandler.executeQuery(retrieveMaxStarIdQuery).get(0).get("max_star_id");
            String newStarId = "nm" + (Integer.parseInt(maxStarId.substring(2)) + 1);

            if (starBirthYearString.isEmpty()) {
                addStarDBHandler.executeUpdate(updateStarsQuery, newStarId, starName, null);
            } else {
                addStarDBHandler.executeUpdate(updateStarsQuery, newStarId, starName, Integer.parseInt(starBirthYearString));
            }
            // Otherwise, report success with the new movie, star, and genre IDs
            responseJsonObj.addProperty("status", "success");
            responseJsonObj.addProperty("message", String.format("Add movie successful! New star ID is %s", newStarId));

            // Write JSON string to output
            out.write(responseJsonObj.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObj.addProperty("status", "failed");
            responseJsonObject.addProperty("message", e.getMessage());
            request.getServletContext().log(e.getMessage());
            out.write(responseJsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }
}