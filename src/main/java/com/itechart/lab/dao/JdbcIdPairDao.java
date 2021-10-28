package com.itechart.lab.dao;

import com.itechart.lab.entity.IdPair;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcIdPairDao extends AbstractEnumDao<IdPair, Integer> {
    JdbcIdPairDao(String tableName, String id1ColumnName, String... id2ColumnName) {
        super(tableName, id1ColumnName, id2ColumnName);
    }

    @Override
    IdPair mapResultSet(ResultSet resultSet) throws SQLException {
        return new IdPair(
                resultSet.getInt(idColumnName),
                resultSet.getInt(valueColumnName)
        );
    }

    @Override
    public int getId(IdPair entity) {
        return entity.getId1();
    }

    @Override
    public Integer getValue(IdPair entity) {
        return entity.getId2();
    }
}
