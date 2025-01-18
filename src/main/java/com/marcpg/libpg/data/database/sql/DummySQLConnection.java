package com.marcpg.libpg.data.database.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A dummy SQL connection for testing purposes or to have something like a null value, but without throwing NullPointerExceptions. <br>
 * This extends the {@link AutoCatchingSQLConnection}, meaning it does not throw anything. <br>
 * Getters will always return null and setters will do nothing.
 * @param <T> The primary key's type.
 */
public class DummySQLConnection<T> extends AutoCatchingSQLConnection<T> {
    /**
     * Creates a dummy connection. This can be used to have something like a null value, but without throwing NullPointerExceptions.
     * @param table The table in the database that will accessed. Can be changed later on using {@link #changeTable(String)}.
     * @param primaryKeyName The primary key's name.
     * @see #closeConnection()
     */
    public DummySQLConnection(String table, String primaryKeyName) {
        super(null, table, primaryKeyName);
    }

    @Override
    public void createNewStatement() {}

    @Override
    public <T2> T2 executeQuery(String sql, Object @NotNull ... params) {
        return null;
    }

    @Override
    public Object[] getRowArray(T primaryKey) {
        return null;
    }

    @Override
    public Map<String, Object> getRowMap(T primaryKey) {
        return Map.of();
    }

    @Override
    public ResultSet getRow(T primaryKey) {
        return null;
    }

    @Override
    public Object get(T primaryKey, String column) {
        return null;
    }

    @Override
    public Object get(T primaryKey, int column) {
        return null;
    }

    @Override
    public void set(T primaryKey, String column, Object newValue) {}

    @Override
    public void set(T primaryKey, int column, Object newValue) {}

    @Override
    public void add(@NotNull Map<String, Object> values) {}

    @Override
    public void remove(T primaryKey) {}

    @Override
    public boolean contains(T primaryKey) {
        return false;
    }

    @Override
    public List<Object[]> getAllRowArrays() {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getAllRowMaps() {
        return List.of();
    }

    @Override
    public List<Object[]> getRowArraysContaining(Object object, String... checkedColumns) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> getRowMapsContaining(Object object, String... checkedColumns) {
        return List.of();
    }

    @Override
    public Collection<Object[]> getRowArraysMatching(String wherePredicate, Object @NotNull ... replacements) {
        return List.of();
    }

    @Override
    public Collection<Map<String, Object>> getRowMapsMatching(String wherePredicate, Object @NotNull ... replacements) {
        return List.of();
    }
}
