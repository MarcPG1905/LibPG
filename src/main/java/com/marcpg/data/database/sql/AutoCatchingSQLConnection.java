package com.marcpg.data.database.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Modified version of the {@link SQLConnection}, but with the key difference
 * of throwing no exceptions and instead handling them automatically inside the
 * methods, using a code snipped provided in the constructor or optionally modified
 * using {@link #changeExceptionHandling(Consumer)}. Can break if the code if the
 * way of handling the exceptions is invalid.
 */
public class AutoCatchingSQLConnection extends SQLConnection {
    private Consumer<SQLException> exceptionHandling;

    /**
     * Creates a connection to a database, that automatically catches SQL exceptions. <br>
     * <b>IMPORTANT: You need to add the database-specific dependency manually!</b>
     * @param type The database type.
     * @param url The URL to the database (jdbc:type://ip:port/name)
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param exceptionHandling What should be done with exceptions, in the rare case of them occurring.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public AutoCatchingSQLConnection(DatabaseType type, String url, String username, String password, String table, Consumer<SQLException> exceptionHandling) throws SQLException, ClassNotFoundException {
        super(type, url, username, password, table);
        this.exceptionHandling = exceptionHandling;
    }

    /**
     * Creates a connection to a database, that automatically catches SQL exceptions. <br>
     * <b>IMPORTANT: You probably need to manually do {@code Class.forName}, for it to properly work!</b>
     * @param type The database type.
     * @param ip The IP of the database. Can be localhost.
     * @param port The port that the database runs on. If set to 0, will use the database type's {@link DatabaseType#defaultPort default port}.
     * @param databaseName The database to name, this should connect to.
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param exceptionHandling What should be done with exceptions, in the rare case of them occurring.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public AutoCatchingSQLConnection(DatabaseType type, String ip, int port, String databaseName, String username, String password, String table, Consumer<SQLException> exceptionHandling) throws SQLException, ClassNotFoundException {
        super(type, ip, port, databaseName, username, password, table);
        this.exceptionHandling = exceptionHandling;
    }

    @Override
    public void closeConnection() {
        try {
            super.closeConnection();
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void createNewStatement() {
        try {
            super.createNewStatement();
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    /**
     * Changes what should be done with the {@link SQLException}s, when they are caught. No usage case for normal users.
     * @param exceptionHandling The new way of handling the {@link SQLException}s.
     */
    public void changeExceptionHandling(Consumer<SQLException> exceptionHandling) {
        this.exceptionHandling = exceptionHandling;
    }

    @Override
    public <T> T executeQuery(String sql, Object... params) {
        try {
            return super.executeQuery(sql, params);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object[] getRowArray(UUID uuid) {
        try {
            return super.getRowArray(uuid);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getRowMap(UUID uuid) {
        try {
            return super.getRowMap(uuid);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return Map.of();
        }
    }

    @Override
    public ResultSet getRow(UUID uuid) {
        try {
            return super.getRow(uuid);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object get(UUID uuid, String column) {
        try {
            return super.get(uuid, column);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object get(UUID uuid, int column) {
        try {
            return super.get(uuid, column);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public void set(UUID uuid, String column, Object newValue) {
        try {
            super.set(uuid, column, newValue);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void set(UUID uuid, int column, Object newValue) {
        try {
            super.set(uuid, column, newValue);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void add(UUID uuid, Object... values) {
        try {
            super.add(uuid, values);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void remove(UUID uuid) {
        try {
            super.remove(uuid);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public boolean contains(UUID uuid) {
        try {
            return super.contains(uuid);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return false;
        }
    }

    @Override
    public List<Object[]> getRowArraysContaining(Object object, String... checkedColumns) {
        try {
            return super.getRowArraysContaining(object, checkedColumns);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getRowMapsContaining(Object object, String... checkedColumns) {
        try {
            return super.getRowMapsContaining(object, checkedColumns);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
        }
    }
}
