package Servlets;

import Helpers.DatabaseHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Models.User;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // You must use HTTP POST instead of HTTP GET so that the username and password will not be displayed on the address bar.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        DatabaseHandler loginDBH = new DatabaseHandler(dataSource);

        PrintWriter out = response.getWriter();

        try {

            String loginQuery = "SELECT * FROM customers\n"
                    + "WHERE email = ?\n";

            List<HashMap<String, String>> loginResult = loginDBH.executeQuery(loginQuery, username);

            JsonObject loginStatusObject = new JsonObject();

            if (loginResult.size() == 1) {

                HashMap<String, String> user = loginResult.get(0);
                
                String userpassword = user.get("password");

                if (userpassword.equals(password)) {

                    // Login success

                    // set this user into the session
                    request.getSession().setAttribute("user", new User(Integer.parseInt(user.get("id")), user.get("firstName"), user.get("lastName"), user.get("ccid"), user.get("address"), user.get("username")));

                    loginStatusObject.addProperty("status", "success");
                    loginStatusObject.addProperty("message", "success");

                    // Log to localhost log
                    request.getServletContext().log("Login successful");

                } else {

                    // Login fail
                    loginStatusObject.addProperty("status", "fail");
                    loginStatusObject.addProperty("message", "Error: Incorrect Password");

                    // Log to localhost log
                    request.getServletContext().log("Login failed - incorrect password");
                }

            } else {

                // Login fail
                loginStatusObject.addProperty("status", "fail");
                loginStatusObject.addProperty("message", "Error: User " + username + " Does Not Exist.");

                // Log to localhost log
                request.getServletContext().log("Login failed - user does not exist");

            }

            // Write JSON string to output
            out.write(loginStatusObject.toString());

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

