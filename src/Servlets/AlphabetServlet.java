package Servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AlphabetServlet", urlPatterns = "/api/alphabet")
public class AlphabetServlet extends HttpServlet {

    public void init(ServletConfig config) {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 0,1,2,3..A,B,C...X,Y,Z

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {

            JsonArray alphabetArray = new JsonArray();

            for (int i = 0; i < 10; i++) {
                alphabetArray.add(Integer.toString(i));
            }

            for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
                alphabetArray.add(alphabet);
            }

            alphabetArray.add("*");

            out.write(alphabetArray.toString());

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
