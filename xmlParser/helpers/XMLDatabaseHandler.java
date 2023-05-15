package helpers;

import models.Movie;

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

    public void executeSimBatchUpdate(HashMap<String, Movie> movieHashMap) throws Exception {

        int batchLimit = 1000;

        Class.forName("com.mysql.cj.jdbc.Driver");

        String query = "INSERT INTO stars_in_movies (starId, movieId)\n" +
                "VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql:///localhost:3306/moviedb?autoReconnect=true&useSSL=false",
                "mytestuser",
                "My6$Password"
        )) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            try {

                for (Map.Entry<String, Movie> entry : movieHashMap.entrySet()) {

                    Movie movie = entry.getValue();

                    String movieId = movie.getId();

                    Set<String> starIDs = movie.getStarIds();

                    Iterator<String> iterator = starIDs.iterator();

                    while (iterator.hasNext()) {

                        String starId = iterator.next();

                        preparedStatement.setString(1, starId);
                        preparedStatement.setString(2, movieId);

                        preparedStatement.addBatch();

                        batchLimit--;

                        if (batchLimit == 0) {
                            preparedStatement.executeBatch();
                            preparedStatement.clearBatch();
                            batchLimit = 1000;
                        }

                        preparedStatement.clearParameters();
                    }
                }

            } finally {
                preparedStatement.executeBatch();
                preparedStatement.close();
            }

            System.out.println("Executed Batch DML Query: \n" + query);
        }
    }
}
