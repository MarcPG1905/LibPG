package com.marcpg.libpg.data.database.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a {@link Connection connection} to a database with built-in features for
 * accessing the database with proper values with primary keys, etc. <br>
 * This database connection is meant to be used with databases that follow the simple
 * scheme of having a the type {@code <T>} as the first column and set to be the primary-key.
 * @param <T> The primary key's type.
 * @since 0.0.6
 * @author MarcPG1905
 */
public class SQLConnection<T> {
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
     * The table's primary key name, like "uuid" for example.
     * It's the first column and it should always be unique per row.
     */
    private final String primaryKeyName;

    /**
     * Creates a connection to a SQL compatible database.
     * @param type The database type.
     * @param url The URL to the database (jdbc:type://ip:port/name)
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param primaryKeyName The primary key's name.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public SQLConnection(@NotNull DatabaseType type, String url, String username, String password, String table, String primaryKeyName) throws SQLException, ClassNotFoundException {
        Class.forName(type.driverClass);
        this.connection = DriverManager.getConnection(url, username, password);
        this.table = table;
        this.primaryKeyName = primaryKeyName;
    }

    /**
     * Creates a connection to a SQL compatible database.
     * @param type The database type.
     * @param ip The IP of the database. Can be localhost.
     * @param port The port that the database runs on. If set to 0, will use the database type's {@link DatabaseType#defaultPort default port}.
     * @param databaseName The database to name, this should connect to.
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param primaryKeyName The primary key's name.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public SQLConnection(DatabaseType type, String ip, int port, String databaseName, String username, String password, String table, String primaryKeyName) throws SQLException, ClassNotFoundException {
        this(type, "jdbc:" + type.urlPart + "://" + ip + ":" + (port == 0 ? type.defaultPort : port) + "/" + databaseName, username, password, table, primaryKeyName);
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
     * Get the primary key's name.
     * @return The table's primary key's name.
     */
    public String primaryKeyName() {
        return primaryKeyName;
    }

    /**
     * Executes a parameterized SQL query and returns the result. When the
     * result has multiple values, it will choose the first column index.
     * @param sql The parameterized query to be executed.
     * @param params The parameters that should be set into the sql query input.
     * @param <T2> The type of the result to be returned.
     * @return The result of the query execution, or {@code null} if it's empty.
     * @throws SQLException if there was an error while executing the query.
     */
    @SuppressWarnings("unchecked")
    public <T2> T2 executeQuery(String sql, Object @NotNull ... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? (T2) resultSet.getObject(1) : null;
            }
        }
    }

    /**
     * Get a whole row from the table based on the primary key.
     * @param primaryKey The primary key to get the row from.
     * @return All {@link Object objects} of the row as an {@link Array array}.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object[] getRowArray(T primaryKey) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, primaryKey);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? extractRow(resultSet) : null;
            }
        }
    }

    /**
     * Get a whole row with column names and values from the table based on the primary key.
     * @param primaryKey The primary key to get the row from.
     * @return All {@link Object objects} of the row as a {@link Map map}.
     * @throws SQLException if there was an error while executing the query.
     */
    public Map<String, Object> getRowMap(T primaryKey) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, primaryKey);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? extractRowAsMap(resultSet) : null;
            }
        }
    }

    /**
     * Get the {@link ResultSet result} of getting a row based on the primary key.
     * @param primaryKey The primary key to get the row from.
     * @return The {@link ResultSet result} of the query.
     * @throws SQLException if there was an error while executing the query.
     */
    public ResultSet getRow(T primaryKey) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, primaryKey);
            return preparedStatement.executeQuery();
        }
    }

    /**
     * Get a specified column, based on the primary key and the column name.
     * @param primaryKey The primary key to get the value from.
     * @param column The column/value's name.
     * @return The {@link Object value} in the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object get(T primaryKey, String column) throws SQLException {
        String sql = "SELECT " + column + " FROM " + table + " WHERE " + primaryKeyName + " = ?";
        return executeQuery(sql, primaryKey);
    }

    /**
     * Get a specified column, based on the primary key and the column index.
     * @param primaryKey The primary key to get the value from.
     * @param column The column/value's index (starting at 1).
     * @return The {@link Object value} in the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public Object get(T primaryKey, int column) throws SQLException {
        String sql = "SELECT " + (column + 1) + " FROM " + table + " WHERE " + primaryKeyName + " = ?";
        return executeQuery(sql, primaryKey);
    }

    /**
     * Set a value of a specified row to a new value, based on the primary key and the column name.
     * @param primaryKey The primary key to get the value from.
     * @param column The column/value's name.
     * @param newValue The new value for the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public void set(T primaryKey, String column, Object newValue) throws SQLException {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, primaryKey);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Set a value of a specified row to a new value, based on the primary key and the column index.
     * @param primaryKey The primary key to get the value from.
     * @param column The column/value's index (starting at 1).
     * @param newValue The new value for the specified field.
     * @throws SQLException if there was an error while executing the query.
     */
    public void set(T primaryKey, int column, Object newValue) throws SQLException {
        String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, primaryKey);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Inserts a new row with the specified values into the table.
     * @param values All values for the new row. The keys are the column names and the values are the values.
     *               Has to be valid according to database settings, otherwise throws an {@link SQLException Exception}.
     * @throws SQLException if there was an error while executing the query. Can be caused by invalid values.
     */
    public void add(@NotNull Map<String, Object> values) throws SQLException {
        StringBuilder placeholders = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (String column : values.keySet()) {
            placeholders.append("?,");
            params.add(values.get(column));
        }
        placeholders.deleteCharAt(placeholders.length() - 1); // Remove trailing comma

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, String.join(",", values.keySet()), placeholders);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Removed a row based on the primary key.
     * @param primaryKey The primary key to remove.
     * @throws SQLException if there was an error while executing the query.
     *                      Can be caused by removing a non-existent row.
     */
    public void remove(T primaryKey) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + primaryKeyName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, primaryKey);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Checks whether the table contains an entry/row with the primary key or not.
     * @param primaryKey The primary key to check for.
     * @return true if the table contains the primary key, false otherwise.
     * @throws SQLException if there was an error while executing the query.
     */
    public boolean contains(T primaryKey) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE " + primaryKeyName + " = ?";
        return executeQuery(sql, primaryKey) != null;
    }

    /**
     * Get all rows of the table, where the {@link ArrayList list} represents
     * the rows and the arrays are each row's columns.
     * @return A {@link ArrayList list} containing all columns as an array.
     * @throws SQLException if there was an error while executing the query.
     */
    public List<Object[]> getAllRowArrays() throws SQLException {
        String sql = "SELECT * FROM " + table;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRows(resultSet);
            }
        }
    }

    /**
     * Get all rows of the table, where the {@link ArrayList list} represents the
     * rows and the {@link HashMap maps} are each row's columns with their name
     * first and then their value.
     * @return A {@link HashMap map} containing all columns with their names and values.
     * @throws SQLException if there was an error while executing the query.
     */
    public List<Map<String, Object>> getAllRowMaps() throws SQLException {
        String sql = "SELECT * FROM " + table;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRowsAsMaps(resultSet);
            }
        }
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
            for (int i = 1; i <= checkedColumns.length; i++)
                preparedStatement.setObject(i, object);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRows(resultSet);
            }
        }
    }

    /**
     * Get all rows of the table containing a specific value in <b>any</b>
     * specified column as a List of maps, where the {@link ArrayList list}
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
            for (int i = 1; i <= checkedColumns.length; i++)
                preparedStatement.setObject(i, object);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRowsAsMaps(resultSet);
            }
        }
    }

    /**
     * Get all rows matching a specified WHERE predicate as a Collection of arrays,
     * where the {@link ArrayList list} represents the rows and the arrays are each
     * row's columns with their name first and then their value.
     * @param wherePredicate The SQL {@code WHERE} predicate, like {@code column = ? AND another_column = ?}.
     *                       This is inserted right after the {@code WHERE}.
     * @param replacements What <b>objects</b> the question marks should be replaced with.
     * @return All rows that match the WHERE predicate as arrays.
     * @throws SQLException if there was an error while executing the query or the WHERE predicate was invalid.
     */
    public Collection<Object[]> getRowArraysMatching(String wherePredicate, Object @NotNull ... replacements) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + wherePredicate;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < replacements.length; i++)
                preparedStatement.setObject(i + 1, replacements[i]);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRows(resultSet);
            }
        }
    }

    /**
     * Get all rows matching a specified WHERE predicate as a Collection of arrays,
     * where the {@link Collection collection} represents the rows and the arrays
     * are each row's columns with their name first and then their value.
     * @param wherePredicate The SQL {@code WHERE} predicate, like {@code column = ? AND another_column = ?}.
     *                       This is inserted right after the {@code WHERE}.
     * @param replacements What <b>objects</b> the question marks should be replaced with.
     * @return All rows that match the WHERE predicate as arrays.
     * @throws SQLException if there was an error while executing the query or the WHERE predicate was invalid.
     */
    public Collection<Map<String, Object>> getRowMapsMatching(String wherePredicate, Object @NotNull ... replacements) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + wherePredicate;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < replacements.length; i++)
                preparedStatement.setObject(i + 1, replacements[i]);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractRowsAsMaps(resultSet);
            }
        }
    }

    private Object @NotNull [] extractRow(@NotNull ResultSet resultSet) throws SQLException {
        int columns = resultSet.getMetaData().getColumnCount();
        Object[] objects = new Object[columns];
        for (int i = 1; i <= columns; i++) {
            Object value = resultSet.getObject(i);
            objects[i - 1] = value;
        }
        return objects;
    }

    private @NotNull Map<String, Object> extractRowAsMap(@NotNull ResultSet resultSet) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            map.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
        }
        return map;
    }

    private @NotNull List<Object[]> extractRows(@NotNull ResultSet resultSet) throws SQLException {
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

    private @NotNull List<Map<String, Object>> extractRowsAsMaps(@NotNull ResultSet resultSet) throws SQLException {
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
