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
import java.util.Objects;

@WebServlet(name = "DashboardAddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class DashboardAddMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getServletContext().log("Get in add movie servlet.");

        String movieTitle = request.getParameter("movieTitle");
        String movieYearString = request.getParameter("movieYear");
        String movieDirector = request.getParameter("movieDirector");
        String starName = request.getParameter("starName");
        String starBirthYearString = request.getParameter("starBirthYear");
        String genreName = request.getParameter("genreName");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObj = new JsonObject();

        if (movieTitle.isEmpty() || movieYearString.isEmpty() || movieDirector.isEmpty() || starName.isEmpty() || genreName.isEmpty()) {
            responseJsonObj.addProperty("status", "failed");
            responseJsonObj.addProperty("message", "Fields except for the star's birth year are all required.");

            // Write JSON string to output
            out.write(responseJsonObj.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            return;
        }

        try {
            int movieYear = Integer.parseInt(movieYearString);

            DatabaseHandler addMovieDBHandler = new DatabaseHandler(dataSource);

            String addMovieQuery = "{call add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            List<Object> outParams;

            if (starBirthYearString.isEmpty()) {
                outParams = addMovieDBHandler.executeStoredProcedure(
                        addMovieQuery,
                        new int[]{Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR},
                        movieTitle,
                        movieYear,
                        movieDirector,
                        starName,
                        null,
                        genreName
                );
            } else {
                outParams = addMovieDBHandler.executeStoredProcedure(
                        addMovieQuery,
                        new int[]{Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR},
                        movieTitle,
                        movieYear,
                        movieDirector,
                        starName,
                        Integer.parseInt(starBirthYearString),
                        genreName
                );
            }

            System.out.println(outParams);

            if ((Boolean) outParams.get(0)) {
                // If the movie already exists in the database, report failure
                responseJsonObj.addProperty("status", "failed");
                responseJsonObj.addProperty("message", "Adding movie failed! The movie added is duplicated in the database.");
                request.getServletContext().log("Movie added failed due to duplicate movie.");
            } else {
                // Otherwise, report success with the new movie, star, and genre IDs
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", String.format("Add movie successful! Movie ID is %s, star ID is %s, genre ID is %s", outParams.get(1), outParams.get(2), outParams.get(3)));
                request.getServletContext().log(String.format("Add movie successful! Movie ID is %s, star ID is %s, genre ID is %s", outParams.get(1), outParams.get(2), outParams.get(3)));
            }

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