import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler {

    private Connection connection;

    public DatabaseHandler(DataSource dataSource) {

        try {
            connection = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<HashMap<String, String>> executeQuery(String query, String... queryStrings) throws Exception {

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        int index = 1;

        for (int i = 1; i<=queryStrings.length; ++i) {
            preparedStatement.setString(index, queryStrings[i-1]);
            index += 1;
        }

        ResultSet rs = preparedStatement.executeQuery();

        ResultSetMetaData md = rs.getMetaData();

        int columns = md.getColumnCount();

        List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        while (rs.next()) {
            HashMap<String,String> row = new HashMap<String, String>(columns);

            for(int i=1; i<=columns; ++i) {
                row.put(md.getColumnName(i), rs.getString(i));
            }

            list.add(row);
        }

        rs.close();
        preparedStatement.close();

        return list;
    }

    protected void close() throws Exception {
        connection.close();
    }
}
