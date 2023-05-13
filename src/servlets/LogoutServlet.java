package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false); // Get the current session, but do not create a new one if it
                                                         // doesn't exist

        if (session != null) {
            session.invalidate(); // Invalidate the session, removing all attributes

            System.out.println("LogoutServlet: Invalidated Session");
        }

        System.out.println("LogoutServlet: Redirected to ./login.html");
        response.sendRedirect("./login.html");
    }
}