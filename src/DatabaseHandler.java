import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseHandler {

    private DataSource dataSource;
    private Connection connection;

    private Statement statement;

    public DatabaseHandler(DataSource ds) {

        try {
            dataSource = ds;
            connection = ds.getConnection();
            statement = connection.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ResultSet executeQuery(String query) throws Exception {

        statement.close();

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    public void closeResources() throws Exception {
        statement.close();
        connection.close();
    }
}
