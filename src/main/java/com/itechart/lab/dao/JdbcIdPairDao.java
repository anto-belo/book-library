package com.itechart.lab.dao;

import com.itechart.lab.connection.ConnectionManager;
import com.itechart.lab.entity.IdPair;
import com.itechart.lab.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcIdPairDao extends AbstractEnumDao<IdPair, Integer> {
    private static final String MSG_FAILED_FIND_SAVED_ID_PAIR = "Failed to find saved ID pair";

    JdbcIdPairDao(String tableName, String id1ColumnName, String... id2ColumnName) {
        super(tableName, id1ColumnName, id2ColumnName);
    }

    @Override
    public IdPair save(IdPair entity) throws DaoException {
        try (final PreparedStatement statement = ConnectionManager.getManager()
                .getConnection()
                .prepareStatement(sqlSave)) {
            prepareSaveStatement(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        Optional<IdPair> thisEntity = findById(entity.getId1());
        if (!thisEntity.isPresent()) {
            throw new DaoException(MSG_FAILED_FIND_SAVED_ID_PAIR);
        } else {
            return thisEntity.get();
        }
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, IdPair entity) throws SQLException {
        statement.setInt(1, entity.getId1());
        statement.setInt(2, entity.getId2());
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
