package com.marcpg.data.database.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Modified version of the {@link SQLConnection}, but with the key difference
 * of throwing no exceptions and instead handling them automatically inside the
 * methods, using a code snipped provided in the constructor or optionally modified
 * using {@link #changeExceptionHandling(Consumer)}. Can break if the code if the
 * way of handling the exceptions is invalid.
 * @since 0.0.6
 * @author MarcPG1905
 */
public class AutoCatchingSQLConnection<T> extends SQLConnection<T> {
    private Consumer<SQLException> exceptionHandling;

    /**
     * Creates a connection to a SQL compatible database, that automatically catches SQL exceptions.
     * @param type The database type.
     * @param url The URL to the database (jdbc:type://ip:port/name)
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param primaryKeyName The primary key's name.
     * @param exceptionHandling What should be done with exceptions, in the rare case of them occurring.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public AutoCatchingSQLConnection(DatabaseType type, String url, String username, String password, String table, String primaryKeyName, Consumer<SQLException> exceptionHandling) throws SQLException, ClassNotFoundException {
        super(type, url, username, password, table, primaryKeyName);
        this.exceptionHandling = exceptionHandling;
    }

    /**
     * Creates a connection to a SQL compatible database, that automatically catches SQL exceptions.
     * @param type The database type.
     * @param ip The IP of the database. Can be localhost.
     * @param port The port that the database runs on. If set to 0, will use the database type's {@link DatabaseType#defaultPort default port}.
     * @param databaseName The database to name, this should connect to.
     * @param username Username of the account to access the database with.
     * @param password Password of the account to access the database with.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param primaryKeyName The primary key's name.
     * @param exceptionHandling What should be done with exceptions, in the rare case of them occurring.
     * @throws SQLException if the connection wasn't successful, which is likely due to a wrong URL or wrong credentials.
     * @throws ClassNotFoundException if the dependency of the database type is missing.
     * @see #closeConnection()
     */
    public AutoCatchingSQLConnection(DatabaseType type, String ip, int port, String databaseName, String username, String password, String table, String primaryKeyName, Consumer<SQLException> exceptionHandling) throws SQLException, ClassNotFoundException {
        super(type, ip, port, databaseName, username, password, table, primaryKeyName);
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
    public <T2> T2 executeQuery(String sql, Object @NotNull ... params) {
        try {
            return super.executeQuery(sql, params);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object[] getRowArray(T primaryKey) {
        try {
            return super.getRowArray(primaryKey);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return new Object[] {};
        }
    }

    @Override
    public Map<String, Object> getRowMap(T primaryKey) {
        try {
            return super.getRowMap(primaryKey);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return Map.of();
        }
    }

    @Override
    public ResultSet getRow(T primaryKey) {
        try {
            return super.getRow(primaryKey);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object get(T primaryKey, String column) {
        try {
            return super.get(primaryKey, column);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public Object get(T primaryKey, int column) {
        try {
            return super.get(primaryKey, column);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return null;
        }
    }

    @Override
    public void set(T primaryKey, String column, Object newValue) {
        try {
            super.set(primaryKey, column, newValue);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void set(T primaryKey, int column, Object newValue) {
        try {
            super.set(primaryKey, column, newValue);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }


    @Override
    public void add(@NotNull Map<String, Object> values) {
        try {
            super.add(values);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public void remove(T primaryKey) {
        try {
            super.remove(primaryKey);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
        }
    }

    @Override
    public boolean contains(T primaryKey) {
        try {
            return super.contains(primaryKey);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return false;
        }
    }

    @Override
    public List<Object[]> getAllRowArrays() {
        try {
            return super.getAllRowArrays();
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getAllRowMaps() {
        try {
            return super.getAllRowMaps();
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
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

    @Override
    public Collection<Object[]> getRowArraysMatching(String wherePredicate, Object @NotNull ... replacements) {
        try {
            return super.getRowArraysMatching(wherePredicate, replacements);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
        }
    }

    @Override
    public Collection<Map<String, Object>> getRowMapsMatching(String wherePredicate, Object @NotNull ... replacements) {
        try {
            return super.getRowMapsMatching(wherePredicate, replacements);
        } catch (SQLException e) {
            exceptionHandling.accept(e);
            return List.of();
        }
    }
}
