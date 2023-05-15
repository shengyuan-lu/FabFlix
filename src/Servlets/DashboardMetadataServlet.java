package Servlets;

import Helpers.DatabaseHandler;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

// Declaring a WebServlet called Servlets.SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "DashboardMetadataServlet", urlPatterns = "/_dashboard/api/dashboard-metadata")
public class DashboardMetadataServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            DatabaseHandler singleMovieDBHandler = new DatabaseHandler(dataSource);

            JsonObject metadataObj = new JsonObject();

            String metadataInfoQuery = "show tables;";
            // There is going to be only one row in the query result
            List<HashMap<String, String>> tables = singleMovieDBHandler.executeQuery(metadataInfoQuery);

            for (HashMap<String, String> table : tables) {
                JsonObject singleTableInfoObject = new JsonObject();

                String tableName = table.get("Tables_in_moviedb");

                String tableInfoQuery = "select column_name, column_type from information_schema.columns where table_schema = 'moviedb' and table_name = '" + tableName + "';";
                List<HashMap<String, String>> tableInfo = singleMovieDBHandler.executeQuery(tableInfoQuery);
                for (HashMap<String, String> each : tableInfo) {
                    String columnName = each.get("COLUMN_NAME");
                    String columnType = each.get("COLUMN_TYPE");
                    request.getServletContext().log("column name: " + columnName);
                    request.getServletContext().log("column type: " + columnType);
                    singleTableInfoObject.addProperty(columnName, columnType);
                }

                metadataObj.add(tableName, singleTableInfoObject);
            }

            // Write JSON string to output
            out.write(metadataObj.toString());
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
