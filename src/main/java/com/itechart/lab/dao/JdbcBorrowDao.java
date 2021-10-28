package com.itechart.lab.dao;

import com.itechart.lab.entity.Borrow;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class JdbcBorrowDao extends AbstractDao<Borrow> {
    private static final String TABLE_NAME = "borrow";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_READER_ID = "reader_id";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_BORROW_DATE = "borrow_date";
    private static final String COLUMN_FOR_MONTHS = "for_months";
    private static final String COLUMN_RETURN_DATE = "return_date";
    private static final String COLUMN_COMMENT = "borrow_comment";
    private static final String COLUMN_BORROW_STATUS = "borrow_status";

    private static JdbcBorrowDao instance;

    private JdbcBorrowDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_READER_ID, COLUMN_BOOK_ID, COLUMN_BORROW_DATE,
                COLUMN_FOR_MONTHS, COLUMN_RETURN_DATE, COLUMN_COMMENT, COLUMN_BORROW_STATUS);
    }

    public static JdbcBorrowDao getInstance() {
        if (instance == null) {
            instance = new JdbcBorrowDao();
        }
        return instance;
    }

    @Override
    Borrow mapResultSet(ResultSet resultSet) throws SQLException {
        return new Borrow(
                resultSet.getInt(COLUMN_ID),
                resultSet.getInt(COLUMN_READER_ID),
                resultSet.getInt(COLUMN_BOOK_ID),
                resultSet.getTimestamp(COLUMN_BORROW_DATE),
                resultSet.getInt(COLUMN_FOR_MONTHS),
                resultSet.getTimestamp(COLUMN_RETURN_DATE),
                resultSet.getString(COLUMN_COMMENT),
                resultSet.getInt(COLUMN_BORROW_STATUS)
        );
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, Borrow entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, entity.getReaderId());
        statement.setInt(3, entity.getBookId());
        if (entity.getBorrowDate() == null) {
            statement.setNull(4, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(4, entity.getBorrowDate());
        }
        statement.setInt(5, entity.getForMonths());
        if (entity.getReturnDate() == null) {
            statement.setNull(6, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(6, entity.getReturnDate());
        }
        if (entity.getComment() == null) {
            entity.setComment("");
        }
        statement.setString(7, entity.getComment());
        statement.setNull(8, Types.INTEGER);
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, Borrow entity) throws SQLException {
        statement.setInt(1, entity.getReaderId());
        statement.setInt(2, entity.getBookId());
        statement.setTimestamp(3, entity.getBorrowDate());
        statement.setInt(4, entity.getForMonths());
        statement.setTimestamp(5, entity.getReturnDate());
        statement.setString(6, entity.getComment());
        statement.setInt(7, entity.getBorrowStatus());
        statement.setInt(8, entity.getId());
    }
}
