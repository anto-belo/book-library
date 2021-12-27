package com.itechart.lab.dao;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.util.RangeFormatter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractEnumDao<T extends DBEntity, V> extends AbstractDao<T>
        implements EnumDao<T, V> {
    protected final String valueColumnName;

    protected final String sqlFindByIdsRange;
    protected final String sqlFindByValue;

    protected AbstractEnumDao(String tableName, String idColumnName, String... otherColumnNames) {
        super(tableName, idColumnName, otherColumnNames);
        valueColumnName = otherColumnNames[0];

        sqlFindByIdsRange = String.format(SQL_SELECT_IN,
                columnNames, tableName, idColumnName, SQL_STUB);
        sqlFindByValue = String.format(SQL_SELECT_BY, columnNames, tableName, otherColumnNames[0]);
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, T entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setObject(2, getValue(entity));
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, T entity) throws SQLException {
        statement.setObject(1, getValue(entity));
        statement.setInt(2, getId(entity));
    }

    @Override
    public List<T> findByIdsRange(List<Integer> ids) throws DaoException {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return findPreparedEntities(null,
                sqlFindByIdsRange.replace(SQL_STUB, RangeFormatter.format(ids)));
    }

    @Override
    public T findByValueOrAdd(T entity) throws DaoException {
        Optional<T> dbEntity = takeFirst(findPreparedEntities(
                s -> s.setObject(1, getValue(entity)), sqlFindByValue));
        if (dbEntity.isPresent()) {
            return dbEntity.get();
        }
        return save(entity);
    }
}
