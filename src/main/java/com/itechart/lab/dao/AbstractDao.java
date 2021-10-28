package com.itechart.lab.dao;

import com.itechart.lab.connection.ConnectionManager;
import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public abstract class AbstractDao<T extends DBEntity> implements Dao<T> {
    static final String SQL_TEMPLATE_SAVE = "INSERT INTO %s (%s) VALUES (%s)";
    static final String SQL_TEMPLATE_FIND_ALL = "SELECT %s FROM %s";
    static final String SQL_TEMPLATE_WHERE_CLAUSE = " WHERE %s";
    static final String SQL_TEMPLATE_WHERE_EQUALITY = " WHERE %s = ?";
    static final String SQL_TEMPLATE_LIKE = " WHERE %s LIKE ?";
    static final String SQL_TEMPLATE_SUB_QUERY_IN = " WHERE %s IN (%s)";
    static final String SQL_TEMPLATE_IN = " WHERE %s IN (?)";

    private static final Connection conn = ConnectionManager.getManager().getConnection();

    private static final String SQL_TEMPLATE_UPDATE_BY = "UPDATE %s SET %s WHERE %s = ?";
    private static final String SQL_TEMPLATE_DELETE_BY = "DELETE FROM %s WHERE %s = ?";

    private static final String MSG_TEMPLATE_CANT_EXEC_SAVE = "Can't execute insert query (%s)";
    private static final String MSG_TEMPLATE_CANT_EXEC_SELECT = "Can't execute select query (%s)";
    private static final String MSG_TEMPLATE_CANT_EXEC_UPDATE = "Can't execute update query (%s)";
    private static final String MSG_TEMPLATE_CANT_EXEC_DELETE = "Can't execute delete query (%s)";

    final String tableName;
    final String idColumnName;

    private final String sqlFindAll;
    private final String sqlFindById;
    private final String sqlSave;
    private final String sqlUpdateById;
    private final String sqlDeleteById;

    AbstractDao(String tableName, String idColumnName, String... otherColumnNames) {
        this.tableName = tableName;
        this.idColumnName = idColumnName;
        String columnNamesString = getColumnNamesString(idColumnName, otherColumnNames);
        sqlFindAll = String.format(SQL_TEMPLATE_FIND_ALL, columnNamesString, tableName);
        sqlFindById = sqlFindAll + String.format(SQL_TEMPLATE_WHERE_EQUALITY, idColumnName);
        sqlSave = String.format(SQL_TEMPLATE_SAVE, tableName, columnNamesString,
                getSavePattern(otherColumnNames.length + 1)); // + id column
        sqlUpdateById = String.format(SQL_TEMPLATE_UPDATE_BY, tableName,
                getUpdatePattern(otherColumnNames), idColumnName);
        sqlDeleteById = String.format(SQL_TEMPLATE_DELETE_BY, tableName, idColumnName);
    }

    static String getColumnNamesString(String columnName, String... otherColumnNames) {
        StringBuilder pattern = new StringBuilder(columnName);
        for (String otherName : otherColumnNames) {
            pattern.append(", ").append(otherName);
        }
        return pattern.toString();
    }

    @Override
    public T save(T entity, String customSqlSave) throws DaoException {
        String sqlSave = customSqlSave == null ? this.sqlSave : customSqlSave;
        int entityId;
        try (final PreparedStatement statement =
                     conn.prepareStatement(sqlSave, Statement.RETURN_GENERATED_KEYS)) {
            prepareSaveStatement(statement, entity);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.first();
            entityId = generatedKeys.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_SAVE, sqlSave));
        }
        Optional<T> thisEntity = findById(entityId);
        if (!thisEntity.isPresent()) {
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_SAVE, sqlSave));
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
            log.error(e.getMessage());
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_SELECT, sqlFindAll));
        }
    }

    @Override
    public Optional<T> findById(int id) throws DaoException {
        return takeFirst(findPreparedEntities(s -> s.setInt(1, id), sqlFindById));
    }

    @Override
    public void update(T entity) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(sqlUpdateById)) {
            prepareUpdateStatement(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_UPDATE, sqlUpdateById));
        }
    }

    @Override
    public void delete(int id) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(sqlDeleteById)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_DELETE, sqlDeleteById));
        }
    }

    List<T> findPreparedEntities(SqlThrowingConsumer<PreparedStatement> preparer,
                                 String query) throws DaoException {
        try (final PreparedStatement statement = conn.prepareStatement(query)) {
            if (preparer != null) {
                preparer.accept(statement);
            }
            try (final ResultSet resultSet = statement.executeQuery()) {
                return mapToEntities(resultSet);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DaoException(String.format(MSG_TEMPLATE_CANT_EXEC_SELECT, query));
        }
    }

    private List<T> mapToEntities(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            final T entity = mapResultSet(resultSet);
            entities.add(entity);
        }
        return entities;
    }

    String getSavePattern(int insertions) {
        StringBuilder pattern = new StringBuilder("?");
        for (int i = 0; i < insertions - 1; i++) {
            pattern.append(", ?");
        }
        return pattern.toString();
    }

    private String getUpdatePattern(String... columnNames) {
        StringBuilder pattern = new StringBuilder(columnNames[0] + " = ?");
        for (int i = 1; i < columnNames.length; i++) {
            pattern.append(",").append(columnNames[i]).append(" = ?");
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
