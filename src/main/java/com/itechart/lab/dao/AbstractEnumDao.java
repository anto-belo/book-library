package com.itechart.lab.dao;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractEnumDao<T extends DBEntity, V> extends AbstractDao<T>
        implements EnumDao<T, V> {
    final String valueColumnName;

    private final String sqlFindByValue;
    private final String sqlFindByValuesRange;

    protected AbstractEnumDao(String tableName, String idColumnName, String... otherColumnNames) {
        super(tableName, idColumnName, otherColumnNames);
        valueColumnName = otherColumnNames[0];
        this.sqlFindByValue = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_WHERE_EQUALITY,
                AbstractDao.getColumnNamesString(idColumnName, otherColumnNames), tableName,
                otherColumnNames[0]);
        this.sqlFindByValuesRange = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_IN,
                AbstractDao.getColumnNamesString(idColumnName, otherColumnNames), tableName,
                otherColumnNames[0]);
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
    public T findByValueOrAdd(T entity) throws DaoException {
        Optional<T> dbEntity =
                findPreparedEntities(s -> s.setObject(1, getValue(entity)), sqlFindByValue)
                        .stream()
                        .filter(Objects::nonNull)
                        .findFirst();
        if (dbEntity.isPresent()) {
            return dbEntity.get();
        }
        return save(entity, null);
    }

    @Override
    public List<T> findByValuesRange(V[] values) throws DaoException {
        if (values.length == 0) {
            return Collections.emptyList();
        }
        return findPreparedEntities(s -> s.setString(1, getValuesRangeString(values)),
                sqlFindByValuesRange);
    }

    String getValuesRangeString(V[] values) {
        if (values.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (values[0].getClass() == String.class) {
            sb.append("'").append(values[0]).append("'");
            for (int i = 1; i < values.length; i++) {
                sb.append(", '").append(values[i]).append("'");
            }
            return sb.toString();
        } else if (values[0].getClass() == Integer.class) {
            sb.append(values[0]);
            for (int i = 1; i < values.length; i++) {
                sb.append(", ").append(values[i]);
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
