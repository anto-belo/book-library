package com.itechart.lab.dao;

import com.itechart.lab.entity.Borrow;
import com.itechart.lab.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class JdbcBorrowDao extends AbstractDao<Borrow> implements BorrowDao {
    static final String TABLE_NAME = "borrow";
    static final String COLUMN_ID = "id";
    static final String COLUMN_READER_ID = "reader_id";
    static final String COLUMN_BOOK_ID = "book_id";
    static final String COLUMN_BORROW_DATE = "borrow_date";
    static final String COLUMN_FOR_MONTHS = "for_months";
    static final String COLUMN_RETURN_DATE = "return_date";
    static final String COLUMN_COMMENT = "borrow_comment";
    static final String COLUMN_BORROW_STATUS = "borrow_status";

    private static final JdbcBorrowDao instance = new JdbcBorrowDao();

    private static final String SQL_ORDER_BY = " ORDER BY %s %s";
    private static final String SQL_UNION = " UNION ";
    private static final String SQL_FIND_BOOK_UNCLOSED_BORROWS = SQL_SELECT_IS + " AND %s = ?";
    private static final String SQL_FIND_BOOK_CLOSED_BORROWS
            = SQL_SELECT_IS + " AND %s = ?" + SQL_ORDER_BY;
    private static final String SQL_FIND_BORROWS_EXPIRING_ON
            = SQL_SELECT + " WHERE DATE_ADD(%s, INTERVAL %s MONTH) = ? AND %s IS NULL";

    private final String sqlFindBorrows;
    private final String sqlFindBorrowsExpiringOn;

    private JdbcBorrowDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_READER_ID, COLUMN_BOOK_ID, COLUMN_BORROW_DATE,
                COLUMN_FOR_MONTHS, COLUMN_RETURN_DATE, COLUMN_COMMENT, COLUMN_BORROW_STATUS);
        String sqlFindUnclosedBorrows = String.format(SQL_FIND_BOOK_UNCLOSED_BORROWS,
                columnNames, TABLE_NAME, COLUMN_RETURN_DATE, SQL_NULL, COLUMN_BOOK_ID);
        String sqlFindClosedBorrows = String.format(SQL_FIND_BOOK_CLOSED_BORROWS,
                columnNames, TABLE_NAME, COLUMN_RETURN_DATE, SQL_NOT_NULL, COLUMN_BOOK_ID,
                COLUMN_RETURN_DATE, SQL_DESC);
        sqlFindBorrows = sqlFindUnclosedBorrows + SQL_UNION + sqlFindClosedBorrows;
        sqlFindBorrowsExpiringOn = String.format(SQL_FIND_BORROWS_EXPIRING_ON,
                columnNames, TABLE_NAME, COLUMN_BORROW_DATE, COLUMN_FOR_MONTHS, COLUMN_RETURN_DATE);
    }

    public static JdbcBorrowDao getInstance() {
        return instance;
    }

    @Override
    Borrow mapResultSet(ResultSet resultSet) throws SQLException {
        Integer borrowStatus = resultSet.getInt(COLUMN_BORROW_STATUS);
        borrowStatus = borrowStatus == 0 ? null : borrowStatus;
        return new Borrow(
                resultSet.getInt(COLUMN_ID),
                resultSet.getInt(COLUMN_READER_ID),
                resultSet.getInt(COLUMN_BOOK_ID),
                resultSet.getDate(COLUMN_BORROW_DATE),
                resultSet.getInt(COLUMN_FOR_MONTHS),
                resultSet.getDate(COLUMN_RETURN_DATE),
                resultSet.getString(COLUMN_COMMENT),
                borrowStatus
        );
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, Borrow entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setInt(2, entity.getReaderId());
        statement.setInt(3, entity.getBookId());
        statement.setDate(4, entity.getBorrowDate());
        statement.setInt(5, entity.getForMonths());
        statement.setNull(6, Types.DATE);
        statement.setString(7, entity.getComment());
        statement.setNull(8, Types.INTEGER);
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, Borrow entity) throws SQLException {
        statement.setInt(1, entity.getReaderId());
        statement.setInt(2, entity.getBookId());
        statement.setDate(3, entity.getBorrowDate());
        statement.setInt(4, entity.getForMonths());
        statement.setDate(5, entity.getReturnDate());
        statement.setString(6, entity.getComment());
        statement.setInt(7, entity.getBorrowStatus());
        statement.setInt(8, entity.getId());
    }

    @Override
    public List<Borrow> findBorrows(int bookId) throws DaoException {
        return findPreparedEntities(s -> {
            s.setInt(1, bookId);
            s.setInt(2, bookId);
        }, sqlFindBorrows);
    }

    @Override
    public List<Borrow> findBorrowsExpiringOn(String dueDate) throws DaoException {
        return findPreparedEntities(s ->
                s.setString(1, dueDate), sqlFindBorrowsExpiringOn);
    }
}
