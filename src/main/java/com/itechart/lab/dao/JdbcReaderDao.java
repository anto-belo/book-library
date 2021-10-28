package com.itechart.lab.dao;

import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public class JdbcReaderDao extends AbstractDao<Reader> implements ReaderDao {
    static final String TABLE_NAME = "reader";
    static final String COLUMN_ID = "id";
    static final String COLUMN_FIRST_NAME = "first_name";
    static final String COLUMN_LAST_NAME = "last_name";
    static final String COLUMN_EMAIL = "email";

    private static JdbcReaderDao instance;

    private final String sqlFindByEmailLike;
    private final String sqlFindByEmail;

    private JdbcReaderDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL);
        String namesString = AbstractDao.getColumnNamesString(COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME,
                COLUMN_EMAIL);
        sqlFindByEmailLike = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_LIKE,
                namesString, TABLE_NAME, COLUMN_EMAIL);
        sqlFindByEmail = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_WHERE_EQUALITY,
                namesString, TABLE_NAME, COLUMN_EMAIL);
    }

    public static JdbcReaderDao getInstance() {
        if (instance == null) {
            instance = new JdbcReaderDao();
        }
        return instance;
    }

    @Override
    Reader mapResultSet(ResultSet resultSet) throws SQLException {
        return new Reader(
                resultSet.getInt(COLUMN_ID),
                resultSet.getString(COLUMN_FIRST_NAME),
                resultSet.getString(COLUMN_LAST_NAME),
                resultSet.getString(COLUMN_EMAIL)
        );
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, Reader entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, entity.getFirstName());
        statement.setString(3, entity.getLastName());
        statement.setString(4, entity.getEmail());
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, Reader entity) throws SQLException {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getEmail());
        statement.setInt(4, entity.getId());
    }

    @Override
    public List<Reader> findReadersByEmailLike(String email) throws DaoException {
        return findPreparedEntities(s -> s.setString(1, email + "%"),
                sqlFindByEmailLike);
    }

    @Override
    public Optional<Reader> findReaderByEmail(String email) throws DaoException {
        return takeFirst(findPreparedEntities(s -> s.setString(1, email),
                sqlFindByEmail));
    }
}
