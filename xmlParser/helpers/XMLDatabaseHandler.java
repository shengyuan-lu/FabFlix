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
}
