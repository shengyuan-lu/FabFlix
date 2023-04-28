package Servlets;

import com.google.gson.JsonArray;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Declaring a WebServlet called ShoppingCartServlet, which maps to url "/api/shopping-cart"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "itemsInShoppingCart" from session
        JsonArray itemIDsInShoppingCart = (JsonArray) session.getAttribute("itemsInShoppingCart");

        // If "itemsInShoppingCart" is not found on session, means this is a new user, thus we create a new itemsInShoppingCart
        // JsonArray for the user
        if (itemIDsInShoppingCart == null) {
            // Add the newly created ArrayList to session, so that it could be retrieved next time
            session.setAttribute("itemsInShoppingCart", new JsonArray());
            itemIDsInShoppingCart = new JsonArray();
            // request.getServletContext().log("getting " + itemsInShoppingCart.size() + " items"); // Log to localhost log
        }

        String newItemId = request.getParameter("new_item_id"); // Get parameter that sent by GET request url

        if (newItemId == null) {
            // Show shopping cart page if there's no get parameters
            response.setContentType("application/json");
            // Output stream
            PrintWriter out = response.getWriter();
            // Write JSON string to output
            out.write(itemIDsInShoppingCart.toString());
        } else {
            // In order to prevent multiple requests from altering itemsInShoppingCart ArrayList at the same time, we
            // lock the ArrayList while updating
            synchronized (itemIDsInShoppingCart) {
                itemIDsInShoppingCart.add(newItemId); // Add the new item ID to the itemIDsInShoppingCart ArrayList
                request.getServletContext().log(itemIDsInShoppingCart.toString()); // Log to localhost log

                // Display the current itemsInShoppingCart ArrayList
//            if (itemsInShoppingCart.size() == 0) {
//                out.println("<i>No items</i>");
//            } else {
//                out.println("<ul>");
//                for (String previousItem : itemsInShoppingCart) {
//                    out.println("<li>" + previousItem);
//                }
//                out.println("</ul>");
//            }
            }
        }

        response.setStatus(200);
    }
}

