package xmlParser.helpers;

import org.jetbrains.annotations.Nullable;
import xmlParser.models.Movie;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class XMLDatabaseHandler {

    public XMLDatabaseHandler() {}

    public void executeDataLoadQuery(String query) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb?allowLoadLocalInfile=true";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord=null;

        try {
            assert conn != null;
            psInsertRecord=conn.prepareStatement(query);
            psInsertRecord.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String query, @Nullable Object... queryParameters) throws Exception {

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb?allowLoadLocalInfile=true";

        try (Connection conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password")) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for (int i = 1; i <= queryParameters.length; ++i) {
                Object queryString = queryParameters[i - 1];
                if (queryString instanceof Integer) {
                    preparedStatement.setInt(i, (Integer) queryString);
                } else if (queryString instanceof String) {
                    preparedStatement.setString(i, (String) queryString);
                } else if (queryString == null) {
                    preparedStatement.setNull(i, Types.NULL);
                }
            }

            // System.out.println("Executed Query: \n" + preparedStatement.toString().substring(preparedStatement.toString().indexOf(": ") + 2));

            int rowCount = preparedStatement.executeUpdate();

            preparedStatement.close();

            return rowCount;
        }
    }
}
