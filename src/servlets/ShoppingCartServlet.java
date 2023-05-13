package servlets;

import helpers.DatabaseHandler;
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
import java.util.HashMap;

// Declaring a WebServlet called ShoppingCartServlet, which maps to url "/api/shopping-cart"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getServletContext().log("get in shopping cart servlet");

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "itemsInShoppingCart" from session
        HashMap<String, Integer> itemsInShoppingCart = (HashMap<String, Integer>) session.getAttribute("itemsInShoppingCart");

        String addedItemID = request.getParameter("added_item_id");
        String removedItemID = request.getParameter("removed_item_id");
        String addedOneItemID = request.getParameter("added_one_item_id");
        String removedOneItemID = request.getParameter("removed_one_item_id");
        if (addedItemID != null) {
            synchronized (itemsInShoppingCart) {
                if (!itemsInShoppingCart.containsKey(addedItemID)) {
                    itemsInShoppingCart.put(addedItemID, 1);
                } else {
                    itemsInShoppingCart.put(addedItemID, itemsInShoppingCart.get(addedItemID) + 1);
                }
                request.getServletContext().log("items in shopping cart is " + itemsInShoppingCart);
            }
        } else if (removedItemID != null) {
            synchronized (itemsInShoppingCart) {
                // remove an item from shopping cart
                itemsInShoppingCart.remove(removedItemID);
            }
        } else if (addedOneItemID != null) {
            // In order to prevent multiple requests from altering itemsInShoppingCart ArrayList at the same time, we
            // lock the ArrayList while updating
            synchronized (itemsInShoppingCart) {
                // When the movie is added afterward, increment the count
                itemsInShoppingCart.put(addedOneItemID, itemsInShoppingCart.get(addedOneItemID) + 1);
            }
        } else if (removedOneItemID != null) {
            synchronized (itemsInShoppingCart) {
                // When the movie is deleted, decrement the count
                itemsInShoppingCart.put(removedOneItemID, itemsInShoppingCart.get(removedOneItemID) - 1);
                if (itemsInShoppingCart.get(removedOneItemID) == 0) {
                    // If the number of an item is reduced to zero, remove it from backend
                    itemsInShoppingCart.remove(removedOneItemID);
                }
                // Add the new item ID to the itemsInShoppingCart ArrayList
                request.getServletContext().log(itemsInShoppingCart.toString()); // Log to localhost log
            }
        } else {
            // Show shopping cart page if there's no get parameters
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            try {
                DatabaseHandler singleMovieDBHandler = new DatabaseHandler(dataSource);

                JsonArray shoppingCartArr = new JsonArray();
                for (String itemId : itemsInShoppingCart.keySet()) {
                    String singleMovieInfoQuery = "SELECT m.title, m.price FROM movies AS m \n" +
                            "WHERE m.id = ?";
                    // There is going to be only one row in the query result
                    HashMap<String, String> movieInCartInfo = singleMovieDBHandler.executeQuery(singleMovieInfoQuery, itemId).get(0);
                    JsonObject movieInCartObj = new JsonObject();

                    movieInCartObj.addProperty("movie_id", itemId);
                    movieInCartObj.addProperty("movie_title", movieInCartInfo.get("title"));
                    movieInCartObj.addProperty("movie_price", movieInCartInfo.get("price"));
                    movieInCartObj.addProperty("movie_quantity", itemsInShoppingCart.get(itemId));
                    // movieInCartObj.addProperty("movie_total", itemsInShoppingCart.get(itemId) * Float.parseFloat(movieInCartInfo.get("price")));
                    shoppingCartArr.add(movieInCartObj);
                }

                // Write JSON string to output
                out.write(shoppingCartArr.toString());
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

        response.setStatus(200);
    }
}

