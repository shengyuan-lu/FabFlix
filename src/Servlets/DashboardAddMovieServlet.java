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
import java.util.HashMap;
import java.util.Objects;
@WebServlet(name = "DashboardAddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class DashboardAddMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

            String checkIsMovieExistsQuery = "select exists(SELECT * from movies\n" +
                    "WHERE movies.title = ? and movies.year = ? and movies.director = ?) as isMovieExists;";
            String isMovieExistsResult = addMovieDBHandler.executeQuery(checkIsMovieExistsQuery, movieTitle, movieYear, movieDirector).get(0).get("isMovieExists");

            if (Objects.equals(isMovieExistsResult, "1")) {
                // If a movie doesn't exist in the database yet, report failure
                responseJsonObj.addProperty("status", "failed");
                responseJsonObj.addProperty("message", "Adding movie failed! The movie added is duplicated in the database.");
                request.getServletContext().log("Movie added failed due to duplicate movie.");
            } else {
                // Otherwise, report success
                String addMovieQuery = "call add_movie(?, ?, ?, ?, ?, ?)";
                String getIdsQuery = "select movies.id as movieId, max(stars.id) as starId, genres.id as genreId from movies, stars, genres \n" +
                        "where movies.title = ? and movies.year = ? and movies.director = ?\n" +
                        "and stars.name = ? and stars.birthYear = ?\n" +
                        "and genres.name = ?";

                HashMap<String, String> ids;

                if (starBirthYearString.isEmpty()) {
                    addMovieDBHandler.executeUpdate(addMovieQuery, movieTitle, movieYear, movieDirector, starName, null, genreName);
                    ids = addMovieDBHandler.executeQuery(getIdsQuery, movieTitle, movieYear, movieDirector, starName, null, genreName).get(0);
                } else {
                    addMovieDBHandler.executeUpdate(addMovieQuery, movieTitle, movieYear, movieDirector, starName, Integer.parseInt(starBirthYearString), genreName);
                    ids = addMovieDBHandler.executeQuery(getIdsQuery, movieTitle, movieYear, movieDirector, starName, Integer.parseInt(starBirthYearString), genreName).get(0);
                }

                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", String.format("Add movie successful! Movie ID is %s, star ID is %s, genre ID is %s", ids.get("movieId"), ids.get("starId"), ids.get("genreId")));
                request.getServletContext().log("Movie added successfully!");
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