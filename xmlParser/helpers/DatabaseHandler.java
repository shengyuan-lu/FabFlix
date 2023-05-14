package helpers;

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

    public <T> void executeBatchUpdate(String query, T[][] queryParameters) throws Exception {
        int batchLimit = 1000;

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            try {
                for (int i = 0; i < queryParameters.length; i++) {

                    for (int j = 0; j < queryParameters[i].length; j++) {

                        T queryString = queryParameters[i][j];

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
