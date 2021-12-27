package com.itechart.lab.dao;

import com.itechart.lab.connection.ConnectionManager;
import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.util.RangeFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractDao<T extends DBEntity> implements Dao<T> {
    protected static final String SQL_INSERT = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String SQL_SELECT = "SELECT %s FROM %s";
    protected static final String SQL_UPDATE = "UPDATE %s SET %s WHERE %s = ?";
    protected static final String SQL_DELETE_BY = "DELETE FROM %s WHERE %s = ?";
    protected static final String SQL_DELETE_IN = "DELETE FROM %s WHERE %s IN (%s)";

    protected static final String SELECT_FROM_WHERE = "SELECT %s FROM %s WHERE %s";
    protected static final String SQL_SELECT_BY = SELECT_FROM_WHERE + " = ?";
    protected static final String SQL_SELECT_IN = SELECT_FROM_WHERE + " IN (%s)";
    protected static final String SQL_SELECT_IS = SELECT_FROM_WHERE + " IS %s";
    protected static final String SQL_SELECT_LIKE = SELECT_FROM_WHERE + " LIKE ?";

    protected static final String SQL_FUNCTION_COUNT = "COUNT(*)";

    protected static final String SQL_STUB = "#STUB#";
    protected static final String SQL_DESC = "DESC";
    protected static final String SQL_NULL = "NULL";
    protected static final String SQL_NOT_NULL = "NOT NULL";

    private static final Connection conn = ConnectionManager.getManager().getConnection();

    private static final String MSG_FAILED_FIND_ADDED_ENTITY = "Failed to find added entity";

    protected final String columnNames;
    protected final String idColumnName;

    protected final String sqlSave;
    protected final String sqlFindAll;
    protected final String sqlFindById;
    protected final String sqlUpdateById;
    protected final String sqlDeleteById;
    protected final String sqlDeleteByIdsRange;
    protected final String sqlGetRecordsAmount;

    AbstractDao(String tableName, String idColumnName, String... otherColumnNames) {
        List<String> columnNamesList = new ArrayList<>();
        Collections.addAll(columnNamesList, otherColumnNames);
        columnNamesList.add(0, idColumnName);
        columnNames = RangeFormatter.format(columnNamesList, false);
        this.idColumnName = idColumnName;

        sqlSave = String.format(SQL_INSERT, tableName, columnNames,
                getSavePattern(columnNamesList.size()));
        sqlFindAll = String.format(SQL_SELECT, columnNames, tableName);
        sqlFindById = String.format(SQL_SELECT_BY, columnNames, tableName, idColumnName);
        sqlUpdateById = String.format(SQL_UPDATE, tableName,
                getUpdatePattern(otherColumnNames), idColumnName);
        sqlDeleteById = String.format(SQL_DELETE_BY, tableName, idColumnName);
        sqlDeleteByIdsRange = String.format(SQL_DELETE_IN, tableName, idColumnName, SQL_STUB);
        sqlGetRecordsAmount = String.format(SQL_SELECT, SQL_FUNCTION_COUNT, tableName);
    }

    @Override
    public T save(T entity) throws DaoException {
        int entityId;
        try (final PreparedStatement statement =
                     conn.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {
            prepareSaveStatement(statement, entity);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.first();
            entityId = generatedKeys.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        Optional<T> thisEntity = findById(entityId);
        if (!thisEntity.isPresent()) {
            throw new DaoException(MSG_FAILED_FIND_ADDED_ENTITY);
        } else {
            return thisEntity.get();
        }
    }

    @Override
    public List<T> findAll() throws DaoException {
        try (final Statement statement = conn.createStatement();
             final ResultSet resultSet = statement.executeQuery(sqlFindAll)) {
            return mapToEntities(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public List<T> findRangeById(int id) throws DaoException {
        return findPreparedEntities(s -> s.setInt(1, id), sqlFindById);
    }

    @Override
    public Optional<T> findById(int id) throws DaoException {
        return takeFirst(findRangeById(id));
    }

    @Override
    public void update(T entity) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(sqlUpdateById)) {
            prepareUpdateStatement(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        executeDelete(s -> s.setInt(1, id), sqlDeleteById);
    }

    @Override
    public void deleteByIdsRange(List<Integer> ids) throws DaoException {
        if (ids.isEmpty()) {
            return;
        }
        String sql = sqlDeleteByIdsRange.replace(SQL_STUB, RangeFormatter.format(ids));
        executeDelete(null, sql);
    }

    private void executeDelete(SqlThrowingConsumer<PreparedStatement> preparer,
                               String sql) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(sql)) {
            if (preparer != null) {
                preparer.accept(statement);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public int getRowsAmount() throws DaoException {
        return getRowsAmount(sqlGetRecordsAmount);
    }

    protected int getRowsAmount(String sql) throws DaoException {
        try (final Statement statement = conn.createStatement();
             final ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    protected List<T> findPreparedEntities(SqlThrowingConsumer<PreparedStatement> preparer,
                                           String query) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(query)) {
            if (preparer != null) {
                preparer.accept(statement);
            }
            try (final ResultSet resultSet = statement.executeQuery()) {
                return mapToEntities(resultSet);
            }
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    private List<T> mapToEntities(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(mapResultSet(resultSet));
        }
        return entities;
    }

    private String getSavePattern(int insertions) {
        StringBuilder pattern = new StringBuilder("?");
        for (int i = 0; i < insertions - 1; i++) {
            pattern.append(", ?");
        }
        return pattern.toString();
    }

    private String getUpdatePattern(String... columnNames) {
        StringBuilder pattern = new StringBuilder(columnNames[0] + " = ?");
        for (int i = 1; i < columnNames.length; i++) {
            pattern.append(", ").append(columnNames[i]).append(" = ?");
        }
        return pattern.toString();
    }

    protected Optional<T> takeFirst(List<T> entities) {
        return entities.stream()
                .filter(Objects::nonNull)
                .findFirst();
    }

    abstract T mapResultSet(ResultSet resultSet) throws SQLException;

    abstract void prepareSaveStatement(PreparedStatement statement, T entity) throws SQLException;

    abstract void prepareUpdateStatement(PreparedStatement statement, T entity) throws SQLException;
}
