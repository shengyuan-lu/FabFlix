package Servlets;

import Helpers.DatabaseHandler;
import Helpers.RecaptchaVerifyUtils;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/_dashboard/api/login")
public class DashboardLoginServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDBMaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // You must use HTTP POST instead of HTTP GET so that the username and password will not be displayed on the address bar.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        try {

            RecaptchaVerifyUtils.verify(gRecaptchaResponse);

        } catch (Exception e) {

            JsonObject loginStatusObject = new JsonObject();

            loginStatusObject.addProperty("status", "fail");
            loginStatusObject.addProperty("message", "reCAPTCHA verification failed. Please try again.");

            out.write(loginStatusObject.toString());

            out.close();

            return;
        }

        String employeeEmail = request.getParameter("email");
        String password = request.getParameter("password");

        DatabaseHandler loginDBH = new DatabaseHandler(dataSource);

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        try {

            String loginQuery = "SELECT * FROM employees\n"
                    + "WHERE email = ?\n";

            List<HashMap<String, String>> loginResult = loginDBH.executeQuery(loginQuery, employeeEmail);

            JsonObject loginStatusObject = new JsonObject();

            if (loginResult.size() == 1) {

                HashMap<String, String> user = loginResult.get(0);

                String encryptedPassword = user.get("password");

                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success) {
                    session.setAttribute("employee", true);

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
                loginStatusObject.addProperty("message", "Error: User " + employeeEmail + " Does Not Exist.");

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

