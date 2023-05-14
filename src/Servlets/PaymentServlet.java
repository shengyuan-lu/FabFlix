package Servlets;

import Helpers.DatabaseHandler;
import Models.Customer;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import java.time.LocalDateTime;

// Declaring a WebServlet called ShoppingCartServlet, which maps to url "/api/shopping-cart"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
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
        String cardExpiryDate = (Objects.equals(request.getParameter("cardExpiryDate"), "")) ? "00-00-0000"
                : request.getParameter("cardExpiryDate");

        HttpSession session = request.getSession();
        int customerId = ((Customer) session.getAttribute("customer")).getId();
        HashMap<String, Integer> itemsInShoppingCart = (HashMap<String, Integer>) session.getAttribute("itemsInShoppingCart");
        PrintWriter out = response.getWriter();

        try {
            DatabaseHandler paymentDBHandler = new DatabaseHandler(dataSource);

            String creditCardInfoQuery = "SELECT * FROM creditcards as cc\n" +
                    "WHERE cc.id = ? and cc.firstName = ? and cc.lastName = ? and cc.expiration = ?;";
            // There is going to be only one row in the query result
            List<HashMap<String, String>> creditCardInfo = paymentDBHandler.executeQuery(creditCardInfoQuery,
                    cardNumber, cardHolderFirstName, cardHolderLastName, cardExpiryDate);
            JsonObject responseJsonObj = new JsonObject();

            if (creditCardInfo.size() > 0) {
                // Credit card info is correct
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", "success");
                request.getServletContext().log("Credit card info is correct.");

                List<HashMap<String, String>> mostRecentOrders = new ArrayList<>();

                for (String itemId : itemsInShoppingCart.keySet()) {
                    // Add each item in shopping cart to the sales table
                    String salesUpdateQuery = "INSERT INTO sales (customerId, movieId, saleDate, quantity)\n" +
                            "VALUES (?, ?, ?, ?);\n";

                    paymentDBHandler.executeUpdate(salesUpdateQuery, String.valueOf(customerId), itemId, LocalDateTime.now().toString(), itemsInShoppingCart.get(itemId).toString());

                    HashMap<String, String> order = new HashMap<>();
                    order.put("customerId", String.valueOf(customerId));
                    order.put("movieId", itemId);
                    order.put("saleDate", String.valueOf(java.time.LocalDate.now()));
                    order.put("quantity", itemsInShoppingCart.get(itemId).toString());

                    mostRecentOrders.add(order);
                }

                session.setAttribute("mostRecentOrders", mostRecentOrders); // Reset the most recent order in the sessions

            } else {
                // Credit card info is incorrect
                responseJsonObj.addProperty("status", "fail");
                responseJsonObj.addProperty("message", "Credit card information is incorrect!");
                request.getServletContext().log("Credit Card Information Is Incorrect!");
            }

            // Write JSON string to output
            out.write(responseJsonObj.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (SQLException e) {
            // Write error message JSON object to output
            JsonObject responseJsonObj = new JsonObject();

            // Credit card info is incorrect
            responseJsonObj.addProperty("status", "fail");
            responseJsonObj.addProperty("message", "Credit card information is incorrect!");
            out.write(responseJsonObj.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }
}