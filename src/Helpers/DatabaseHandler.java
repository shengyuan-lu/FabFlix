package Helpers;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler {

    private final DataSource dataSource;

    public DatabaseHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<HashMap<String, String>> executeQuery(String query, T... queryStrings) throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for (int i = 1; i <= queryStrings.length; ++i) {
                T queryString = queryStrings[i-1];
                if (queryString instanceof Integer) {
                    preparedStatement.setInt(i, (Integer) queryString);
                } else if (queryString instanceof String) {
                    preparedStatement.setString(i, (String) queryString);
                } else if (queryString == null) {
                    preparedStatement.setNull(i, Types.NULL);
                }
            }

            System.out.println("Executed Query: \n" + preparedStatement.toString().substring( preparedStatement.toString().indexOf( ": " ) + 2 ));

            ResultSet rs = preparedStatement.executeQuery();

            ResultSetMetaData md = rs.getMetaData();

            int columns = md.getColumnCount();

            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

            while (rs.next()) {
                HashMap<String, String> row = new HashMap<String, String>(columns);

                for (int i = 1; i <= columns; ++i) {
                    row.put(md.getColumnLabel(i), rs.getString(i));
                }

                list.add(row);
            }

            rs.close();
            preparedStatement.close();

            return list;
        }
    }

    // Execute DML statements like INSERT, UPDATE or DELETE
    public <T> int executeUpdate(String query, T... queryStrings) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for (int i = 1; i <= queryStrings.length; ++i) {
                T queryString = queryStrings[i-1];
                if (queryString instanceof Integer) {
                    preparedStatement.setInt(i, (Integer) queryString);
                } else if (queryString instanceof String) {
                    preparedStatement.setString(i, (String) queryString);
                } else if (queryString == null) {
                    preparedStatement.setNull(i, Types.NULL);
                }
            }

            System.out.println("Executed Query: \n" + preparedStatement.toString().substring( preparedStatement.toString().indexOf( ": " ) + 2 ));

            int rowCount = preparedStatement.executeUpdate();

            preparedStatement.close();

            return rowCount;
        }
    }
}
