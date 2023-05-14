package Helpers;

import org.jetbrains.annotations.Nullable;

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

    public <T> List<HashMap<String, String>> executeQuery(String query, T... queryParameters) throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for (int i = 1; i <= queryParameters.length; ++i) {
                T queryString = queryParameters[i-1];
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
    public <T> int executeUpdate(String query, T... queryParameters) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            for (int i = 1; i <= queryParameters.length; ++i) {
                T queryString = queryParameters[i-1];
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

    // Execute MySQL stored procedures
    public List<Object> executeStoredProcedure(String query, int[] outParamsTypes, @Nullable Object... queryStrings) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            int inParamsLength = queryStrings.length;
            int outParamsLength = outParamsTypes.length;

            CallableStatement callStatement = conn.prepareCall(query);

            for (int i = 1; i <= inParamsLength; ++i) {
                Object queryString = queryStrings[i-1];
                if (queryString instanceof Integer) {
                    callStatement.setInt(i, (Integer) queryString);
                } else if (queryString instanceof String) {
                    callStatement.setString(i, (String) queryString);
                } else if (queryString == null) {
                    callStatement.setNull(i, Types.NULL);
                }
            }

            for (int i = 1; i <= outParamsLength; ++i) {
                callStatement.registerOutParameter(inParamsLength + i, outParamsTypes[i-1]);
            }

            callStatement.executeUpdate();

            List<Object> outParams = new ArrayList<>();

            for (int i = 1; i <= outParamsLength; ++i) {
                switch(outParamsTypes[i-1]) {
                    case Types.VARCHAR:
                        System.out.println("types.varchar: " + callStatement.getString(inParamsLength + i));
                        outParams.add(callStatement.getString(inParamsLength + i));
                        break;
                    case Types.INTEGER:
                        System.out.println("types.integer: " + callStatement.getString(inParamsLength + i));
                        outParams.add(callStatement.getInt(inParamsLength + i));
                        break;
                    case Types.BOOLEAN:
                        System.out.println("types.boolean: " + callStatement.getString(inParamsLength + i));
                        outParams.add(callStatement.getBoolean(inParamsLength + i));
                        break;
                }
            }

            callStatement.close();

            return outParams;
        }
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
