package Servlets;

import Helpers.DatabaseHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// Declaring a WebServlet called ShoppingCartServlet, which maps to url "/api/shopping-cart"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getServletContext().log("Get in payment servlet.");

        String cardNumber = request.getParameter("cardNumber");
        String cardHolderFirstName = request.getParameter("cardHolderFirstName");
        String cardHolderLastName = request.getParameter("cardHolderLastName");
        String cardExpiryDate = (Objects.equals(request.getParameter("cardExpiryDate"), "")) ? "00-00-0000" : request.getParameter("cardExpiryDate");
        request.getServletContext().log("Expiry data is " + cardExpiryDate);

        PrintWriter out = response.getWriter();

        try {
            DatabaseHandler singleMovieDBHandler = new DatabaseHandler(dataSource);

            String creditCardInfoQuery = "SELECT * FROM creditcards as cc\n" +
                    "WHERE cc.id = ? and cc.firstName = ? and cc.lastName = ? and cc.expiration = ?;";
            // There is going to be only one row in the query result
            List<HashMap<String, String>> creditCardInfo = singleMovieDBHandler.executeQuery(creditCardInfoQuery, cardNumber, cardHolderFirstName, cardHolderLastName, cardExpiryDate);
            JsonObject responseJsonObj = new JsonObject();

            if (creditCardInfo.size() > 0) {
                // Credit card info is correct
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", "success");
                request.getServletContext().log("Credit card info is correct.");
            } else {
                // Credit card info is incorrect
                responseJsonObj.addProperty("status", "fail");
                responseJsonObj.addProperty("message", "Credit card information entered in incorrect.");
                request.getServletContext().log("Credit card info is incorrect.");
            }
            // Write JSON string to output
            out.write(responseJsonObj.toString());
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