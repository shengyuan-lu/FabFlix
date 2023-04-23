package Servlets;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import Models.User;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    // You must use HTTP POST instead of HTTP GET so that the username and password will not be displayed on the address bar.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // TODO: make the login talk to the database instead of hardcoding username and password
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject responseJsonObject = new JsonObject();

        if (username.equals("anteater") && password.equals("123456")) {
            // Login success

            // set this user into the session
            request.getSession().setAttribute("user", new User(username));

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");

            // Log to localhost log
            request.getServletContext().log("Login failed");

            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (!username.equals("anteater")) {
                responseJsonObject.addProperty("message", "Error: User " + username + " Does Not Exist.");
            } else {
                responseJsonObject.addProperty("message", "Error: Incorrect Password");
            }
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}

