package com.itechart.lab.dao;

import com.itechart.lab.entity.EnumEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcEnumDao extends AbstractEnumDao<EnumEntity, String> {
    JdbcEnumDao(String tableName, String idColumnName, String... valueColumnName) {
        super(tableName, idColumnName, valueColumnName);
    }

    @Override
    EnumEntity mapResultSet(ResultSet resultSet) throws SQLException {
        return new EnumEntity(
                resultSet.getInt(idColumnName),
                resultSet.getString(valueColumnName)
        );
    }

    @Override
    public int getId(EnumEntity entity) {
        return entity.getId();
    }

    @Override
    public String getValue(EnumEntity entity) {
        return entity.getValue();
    }
}
