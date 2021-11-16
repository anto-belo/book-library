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
    static final String COLUMN_NAME = "r_name";
    static final String COLUMN_EMAIL = "email";

    private static final JdbcReaderDao instance = new JdbcReaderDao();

    private static final int EMAILS_LIMIT = 5;

    private final String sqlFindByEmail;
    private final String sqlFindByEmailLike;

    private JdbcReaderDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL);
        sqlFindByEmail = String.format(SQL_SELECT_BY, columnNames, TABLE_NAME, COLUMN_EMAIL);
        sqlFindByEmailLike = String.format(SQL_SELECT_LIKE + " LIMIT %s",
                columnNames, TABLE_NAME, COLUMN_EMAIL, EMAILS_LIMIT);
    }

    public static JdbcReaderDao getInstance() {
        return instance;
    }

    @Override
    Reader mapResultSet(ResultSet resultSet) throws SQLException {
        return new Reader(
                resultSet.getInt(COLUMN_ID),
                resultSet.getString(COLUMN_NAME),
                resultSet.getString(COLUMN_EMAIL)
        );
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, Reader entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, entity.getName());
        statement.setString(3, entity.getEmail());
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, Reader entity) throws SQLException {
        statement.setString(1, entity.getName());
        statement.setString(2, entity.getEmail());
        statement.setInt(3, entity.getId());
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

    @Override
    public Reader findOrAdd(Reader reader) throws DaoException {
        Optional<Reader> optReader = findReaderByEmail(reader.getEmail());
        if (optReader.isPresent()) {
            Reader dbReader = optReader.get();
            if (!optReader.get().getName().equals(reader.getName())) {
                dbReader.setName(reader.getName());
                update(dbReader);
            }
            return dbReader;
        }
        return save(reader);
    }
}
