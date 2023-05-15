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

// Declaring a WebServlet called ShoppingCartServlet, which maps to url "/api/shopping-cart"
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getServletContext().log("Get in Confirmation servlet.");

        HttpSession session = request.getSession();

        List<HashMap<String, String>> mostRecentOrders = (ArrayList<HashMap<String, String>>) session.getAttribute("mostRecentOrders");

        PrintWriter out = response.getWriter();


        try {
            DatabaseHandler paymentDBHandler = new DatabaseHandler(dataSource);


            String orderConfirmationQuery = "SELECT sales.id as saleId, movies.title as movieTitle, sales.quantity as saleQuantity, movies.price as moviePrice FROM sales\n" +
                    "JOIN movies ON movies.id = sales.movieId\n" +
                    "WHERE sales.movieId = ? and sales.customerId = ? and sales.saleDate = ? and sales.quantity = ?;";

            JsonArray responseJsonArr = new JsonArray(); // Response with a list of orders confirmed

            for (HashMap<String, String> order : mostRecentOrders) {
                String movieId = order.get("movieId");
                String customerId = order.get("customerId");
                String saleDate = order.get("saleDate");
                String quantity = order.get("quantity");

                List<HashMap<String, String>> orderConfirmationInfo = paymentDBHandler.executeQuery(orderConfirmationQuery, movieId, customerId, saleDate, quantity);


                JsonObject responseJsonObj = new JsonObject();

                if (orderConfirmationInfo.size() > 0) {
                    // Credit card info is correct
                    HashMap<String, String> mostRecentOrder = orderConfirmationInfo.get(orderConfirmationInfo.size() - 1);
                    responseJsonObj.addProperty("saleId", mostRecentOrder.get("saleId"));
                    responseJsonObj.addProperty("movieTitle", mostRecentOrder.get("movieTitle"));
                    responseJsonObj.addProperty("saleQuantity", mostRecentOrder.get("saleQuantity"));
                    responseJsonObj.addProperty("moviePrice", mostRecentOrder.get("moviePrice"));
                    responseJsonObj.addProperty("movieTotal", Float.parseFloat(mostRecentOrder.get("moviePrice")) * Integer.parseInt(mostRecentOrder.get("saleQuantity")));

                    responseJsonArr.add(responseJsonObj);
                } else {
                    request.getServletContext().log("No order placed.");
                }
            }
            session.setAttribute("itemsInShoppingCart", new HashMap<>());
            // Write JSON string to output
            out.write(responseJsonArr.toString());
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