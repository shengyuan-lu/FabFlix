package helpers;

import java.sql.*;

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

    public void executeBatchUpdate(String query, Object[][] queryParameters) throws Exception {

        int batchLimit = 1000;

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql:///localhost:3306/moviedb?autoReconnect=true&useSSL=false",
                "mytestuser",
                "My6$Password"
        )) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            try {
                for (int i = 0; i < queryParameters.length; i++) {

                    for (int j = 0; j < queryParameters[i].length; j++) {

                        Object queryString = queryParameters[i][j];

                        if (queryString instanceof Integer) {
                            preparedStatement.setInt(j + 1, (Integer) queryString);

                        } else if (queryString instanceof String) {
                            preparedStatement.setString(j + 1, (String) queryString);

                        } else if (queryString == null) {
                            preparedStatement.setNull(j + 1, Types.NULL);
                        }
                    }

                    preparedStatement.addBatch();

                    batchLimit--;

                    if (batchLimit == 0) {
                        preparedStatement.executeBatch();
                        preparedStatement.clearBatch();
                        batchLimit = 1000;
                    }

                    preparedStatement.clearParameters();
                }
            } finally {
                preparedStatement.executeBatch();
                preparedStatement.close();
            }

            System.out.println("Executed Batch DML Query: \n" + query);
        }
    }
}
