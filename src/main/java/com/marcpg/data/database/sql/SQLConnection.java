package com.marcpg.data.database.sql;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a {@link Connection connection} to a database with built-in features for
 * accessing the database with proper values like the player {@link UUID}, etc. <br>
 * This database connection is meant to be used with databases that follow the simple
 * scheme of having a UUID as the first column and set to be the primary-key.
 * @since 0.0.6
 * @author MarcPG1905
 */
public class SQLConnection {
    /**
     * Represents a SQL-Database type, such as MySQL or PostgreSQL. <br>
     * This enumeration is important, to ensure all database types properly handled, connected and loaded.
     * @since 0.0.6
     * @author MarcPG1905
     */
    public enum DatabaseType {
        /** PostgreSQL (Recommended) */
        POSTGRESQL("PostgreSQL", "postgresql", 5432, "org.postgresql.Driver", true),
        /** MySQL */
        MYSQL("MySQL", "mysql", 3306, "com.mysql.jdbc.Driver", true),
        /** MariaDB */
        MARIADB("MariaDB", "mariadb", 3306, "org.mariadb.jdbc.Driver", true),
        /** Microsoft SQL Server */
        MS_SQL_SERVER("Microsoft SQL Server", "mssql", 1433, "com.microsoft.sqlserver.jdbc.SQLServerDriver", true),
        /** Oracle Database */
        ORACLE("Oracle Database", "oracle", 1521, "oracle.jdbc.driver.OracleDriver", true),
        /** Firebird */
        FIREBIRD("Firebird", "firebird", 3050, "org.firebirdsql.jdbc.Driver", true),
        /** H2 Database (Embedded database, no default port) */
        H2("H2 Database", "h2", 0, "org.h2.Driver", false),
        /** SQLite (File-based database, no default port) */
        SQLITE("SQLite", "sqlite", 0, "org.sqlite.JDBC", false),
        /** DB2 (Outdated) */
        DB2("DB2", "db2", 50000, "com.ibm.db2.jcc.DB2Driver", false),
        /** Sybase Adaptive Server Enterprise (Outdated) */
        ASE("Sybase Adaptive Server Enterprise", "sybase", 5000, "com.sybase.jdbc4.jdbc.SybDriver", false);

        /** The full name of the Database type. */
        public final String name;

        /** The name inside URLs that are used to connect to the database. */
        public final String urlPart;

        /** The default port that the database uses. */
        public final int defaultPort;

        /** The fully qualified name of the Driver class. */
        public final String driverClass;

        /** If the database type of tested and can be used without any issues. */
        public final boolean safeToUse;

        DatabaseType(String name, String urlPart, int defaultPort, String driverClass, boolean safeToUse) {
            this.name = name;
            this.urlPart = urlPart;
            this.defaultPort = defaultPort;
            this.driverClass = driverClass;
            this.safeToUse = safeToUse;
        }
    }

    /**
     * The connection that's currently used for the statement.
     * Different from the statement, this can actually not be changed.
     * Should be closed when disposing of it, to prevent data loss.
     * @see #connection()
     * @see #closeConnection()
     */
    private final Connection connection;

    /**
     * The table on the connected database that's currently accessed.
     * Can be changed, although it's not recommended to do this often,
     * as you should just create a new connection for that.
     * @see #table()
     * @see #changeTable(String)
     */
    private String table;

    /**
     * Creates a connection to a database. <br>
     * <b>IMPORTANT: You need to add the database-specific dependency manually!</b>
     * @param type The database type.
     * @param url The URL to the database (jdbc:type://ip:port/name)
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public SQLConnection(DatabaseType type, String url, String username, String password, String table) throws SQLException, ClassNotFoundException {
        Class.forName(type.driverClass);
        connection = DriverManager.getConnection(url, username, password);
        this.table = table;
    }

    /**
     * Creates a connection to a database. <br>
     * <b>IMPORTANT: You probably need to manually do {@code Class.forName}, for it to properly work!</b>
     * @param type The database type.
     * @param ip The IP of the database. Can be localhost.
     * @param port The port that the database runs on. If set to 0, will use the database type's {@link DatabaseType#defaultPort default port}.
     * @param databaseName The database to name, this should connect to.
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public SQLConnection(DatabaseType type, String ip, int port, String databaseName, String username, String password, String table) throws SQLException, ClassNotFoundException {
        this(type, "jdbc:" + type.urlPart + "://" + ip + ":" + (port == 0 ? type.defaultPort : port) + "/" + databaseName, username, password, table);
    }

    /**
     * Closes the connection to the database. Recommended to use this before shutting
     * down the program, to not cause any connection issues with the database.
     * @throws SQLException if there was an issue while closing the connection or the connection is already closed.
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Get the current connection that's being used. No actual use.
     * @return The currently used connection to the database.
     */
    public Connection connection() {
        return connection;
    }

    /**
     * Generates a new statement to use for queries. Can be useful for clearing some memory.
     * @throws SQLException if there was an error while creating the new statement.
     */
    public void createNewStatement() throws SQLException {
        connection.createStatement();
    }

    /**
     * Get the current table to get data from.
     * @return The table that's currently being used.
     * @see #changeTable(String)
     */
    public String table() {
        return table;
    }

    /**
     * Changes what table to get data from and execute all queries on.
     * @param table The new table to get data from.
     * @see #table()
     */
    public void changeTable(String table) {
        this.table = table;
    }

    /**
     * Executes a parameterized SQL query and returns the result. When the
     * result has multiple values, it will choose the first column index.
     * @param sql The parameterized query to be executed.
     * @param params The parameters that should be set into the sql query input.
     * @param <T> The type of the result to be returned.
     * @return The result of the query execution, or {@code null} if it's empty.
     * @throws SQLException if there was an error while executing the query.
     */
    @SuppressWarnings("unchecked")
    public <T> T executeQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? (T) resultSet.getObject(1) : null;
            }
        }
    }

    /**
     * Get a whole row from the table based on a player {@link UUID}.
     * @param uuid The {@link UUID} of the player to get the row from.
     * @return All {@link Object objects} of the row as an {@link Array array}.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object[] getRowArray(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, uuid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? extractRow(resultSet) : null;
            }
        }
    }

    /**
     * Get a whole row with column names and values from the table based on a player {@link UUID}.
     * @param uuid The {@link UUID} of the player to get the row from.
     * @return All {@link Object objects} of the row as a {@link Map map}.
     * @throws SQLException if there was an error while executing the query.
     */
    public Map<String, Object> getRowMap(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, uuid);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? extractRowAsMap(resultSet) : null;
            }
        }
    }

    /**
     * Get the {@link ResultSet result} of getting a row based on a player {@link UUID}.
     * @param uuid The {@link UUID} of the player to get the row from.
     * @return The {@link ResultSet result} of the query.
     * @throws SQLException if there was an error while executing the query.
     */
    public ResultSet getRow(UUID uuid) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, uuid);
            return preparedStatement.executeQuery();
        }
    }

    /**
     * Get a value from a specific player's data, based on a player {@link UUID} and the column name.
     * @param uuid The {@link UUID} of the player to get the value from.
     * @param column The column/value's name.
     * @return The {@link Object value} in the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object get(UUID uuid, String column) throws SQLException {
        String sql = "SELECT " + column + " FROM " + table + " WHERE uuid = ?";
        return executeQuery(sql, uuid);
    }

    /**
     * Get a value from a specific player's data, based on a player {@link UUID} and the column index.
     * @param uuid The {@link UUID} of the player to get the value from.
     * @param column The column/value's index (starting at 1).
     * @return The {@link Object value} in the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object get(UUID uuid, int column) throws SQLException {
        String sql = "SELECT " + (column + 1) + " FROM " + table + " WHERE uuid = ?";
        return executeQuery(sql, uuid);
    }

    /**
     * Set a value from a specific player's data to a new value,
     * based on a player {@link UUID} and the column name.
     * @param uuid The {@link UUID} of the player to get the value from.
     * @param column The column/value's name.
     * @param newValue The new value for the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public void set(UUID uuid, String column, Object newValue) throws SQLException {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, uuid);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Set a value from a specific player's data to a new value,
     * based on a player {@link UUID} and the column index.
     * @param uuid The {@link UUID} of the player to get the value from.
     * @param column The column/value's index (starting at 1).
     * @param newValue The new value for the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public void set(UUID uuid, int column, Object newValue) throws SQLException {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, uuid);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Inserts a new row with a player and the values into the table.
     * @param uuid The player's {@link UUID}, which is the first column.
     * @param values All other values for the new row. Has to be valid according to database settings,
     *               otherwise will throw the following {@link SQLException Exception}.
     * @throws SQLException if there was an error while executing the query. Can be caused by invalid values.
     */
    public void add(UUID uuid, Object... values) throws SQLException {
        String sql = "INSERT INTO " + table + " VALUES (?, " + String.join(", ", Collections.nCopies(values.length, "?")) + ")";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, uuid);
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 2, values[i]);
            }
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Removed a player's row based on his {@link UUID}.
     * @param uuid The player's {@link UUID}.
     * @throws SQLException if there was an error while executing the query.
     *                      Can be caused by removing a non-existent player.
     */
    public void remove(UUID uuid) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, uuid);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Checks whether the table contains an entry/row for the player or not.
     * @param uuid The player's {@link UUID}.
     * @return true if the table contains the player's {@link UUID}, false otherwise.
     * @throws SQLException if there was an error while executing the query.
     */
    public boolean contains(UUID uuid) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE uuid = ?";
        return executeQuery(sql, uuid) != null;
    }

    /**
     * Get all rows of the table containing a specific value in <b>any</b>
     * specified column as a List of arrays, where the {@link ArrayList list}
     * represents the rows and the arrays are each row's columns.
     * @param object What object must be contained in at least one of the rows.
     * @param checkedColumns All columns that are checked for the object.
     * @return A {@link ArrayList list} containing all columns as an array.
     * @throws SQLException if there was an error while executing the query.
     */
    public List<Object[]> getRowArraysContaining(Object object, String... checkedColumns) throws SQLException {
        String condition = Arrays.stream(checkedColumns)
                .map(column -> column + " = ?")
                .collect(Collectors.joining(" OR "));
        String sql = "SELECT * FROM " + table + " WHERE " + condition;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, object);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRows(resultSet);
            }
        }
    }

    /**
     * Get all rows of the table containing a specific value in <b>any</b>
     * specified column as a List of arrays, where the {@link ArrayList list}
     * represents the rows and the {@link HashMap maps} are each row's columns
     * with their name first and then their value.
     * @param object What object must be contained in at least one of the rows.
     * @param checkedColumns All columns that are checked for the object.
     * @return A {@link HashMap map} containing all columns with their names and values.
     * @throws SQLException if there was an error while executing the query.
     */
    public List<Map<String, Object>> getRowMapsContaining(Object object, String... checkedColumns) throws SQLException {
        String condition = Arrays.stream(checkedColumns)
                .map(column -> column + " = ?")
                .collect(Collectors.joining(" OR "));
        String sql = "SELECT * FROM " + table + " WHERE " + condition;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, object);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRowsAsMaps(resultSet);
            }
        }
    }

    private Object [] extractRow(ResultSet resultSet) throws SQLException {
        int columns = resultSet.getMetaData().getColumnCount();
        Object[] objects = new Object[columns];
        for (int i = 1; i <= columns; i++) {
            Object value = resultSet.getObject(i);
            objects[i - 1] = value;
        }
        return objects;
    }

    private Map<String, Object> extractRowAsMap(ResultSet resultSet) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            map.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
        }
        return map;
    }

    private List<Object[]> extractRows(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        List<Object[]> rows = new ArrayList<>();
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> extractRowsAsMaps(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }
}
