package com.itechart.lab.dao;

import com.itechart.lab.entity.Book;
import com.itechart.lab.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class JdbcBookDao extends AbstractDao<Book> implements BookDao {
    static final String TABLE_NAME = "book";
    static final String COLUMN_ID = "id";
    static final String COLUMN_COVER_URL = "cover_url";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_PUBLISHER_ID = "publisher_id";
    static final String COLUMN_PUBLISH_YEAR = "publish_year";
    static final String COLUMN_PAGE_COUNT = "page_count";
    static final String COLUMN_ISBN = "isbn";
    static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    static final String COLUMN_REMAINING_AMOUNT = "remaining_amount";
    static final String COLUMN_BOOK_DESCRIPTION = "book_description";

    private static JdbcBookDao instance;

    final String bookColumnNamesString;

    private final String sqlFindAvailableBooks;

    private JdbcBookDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_COVER_URL, COLUMN_TITLE, COLUMN_PUBLISHER_ID,
                COLUMN_PUBLISH_YEAR, COLUMN_PAGE_COUNT, COLUMN_ISBN, COLUMN_TOTAL_AMOUNT,
                COLUMN_REMAINING_AMOUNT, COLUMN_BOOK_DESCRIPTION);
        bookColumnNamesString = AbstractDao.getColumnNamesString(COLUMN_ID, COLUMN_COVER_URL,
                COLUMN_TITLE, COLUMN_PUBLISHER_ID, COLUMN_PUBLISH_YEAR, COLUMN_PAGE_COUNT,
                COLUMN_ISBN, COLUMN_TOTAL_AMOUNT, COLUMN_REMAINING_AMOUNT, COLUMN_BOOK_DESCRIPTION);
        sqlFindAvailableBooks = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_WHERE_CLAUSE,
                bookColumnNamesString, TABLE_NAME, COLUMN_REMAINING_AMOUNT + " > 0");
    }

    public static JdbcBookDao getInstance() {
        if (instance == null) {
            instance = new JdbcBookDao();
        }
        return instance;
    }

    @Override
    Book mapResultSet(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt(COLUMN_ID),
                resultSet.getString(COLUMN_COVER_URL),
                resultSet.getString(COLUMN_TITLE),
                resultSet.getInt(COLUMN_PUBLISHER_ID),
                resultSet.getInt(COLUMN_PUBLISH_YEAR),
                resultSet.getInt(COLUMN_PAGE_COUNT),
                resultSet.getString(COLUMN_ISBN),
                resultSet.getInt(COLUMN_TOTAL_AMOUNT),
                resultSet.getInt(COLUMN_REMAINING_AMOUNT),
                resultSet.getString(COLUMN_BOOK_DESCRIPTION)
        );
    }

    @Override
    void prepareSaveStatement(PreparedStatement statement, Book entity) throws SQLException {
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, entity.getCoverUrl());
        statement.setString(3, entity.getTitle());
        statement.setInt(4, entity.getPublisherId());
        statement.setInt(5, entity.getPublishYear());
        statement.setInt(6, entity.getPageCount());
        statement.setString(7, entity.getIsbn());
        statement.setInt(8, entity.getTotalAmount());
        statement.setInt(9, entity.getRemainingAmount());
        statement.setString(10, entity.getDescription());
    }

    @Override
    void prepareUpdateStatement(PreparedStatement statement, Book entity) throws SQLException {
        statement.setString(1, entity.getCoverUrl());
        statement.setString(2, entity.getTitle());
        statement.setInt(3, entity.getPublisherId());
        statement.setInt(4, entity.getPublishYear());
        statement.setInt(5, entity.getPageCount());
        statement.setString(6, entity.getIsbn());
        statement.setInt(7, entity.getTotalAmount());
        statement.setInt(8, entity.getRemainingAmount());
        statement.setString(9, entity.getDescription());
        statement.setInt(10, entity.getId());
    }

    @Override
    public List<Book> findAvailableBooks() throws DaoException {
        return findPreparedEntities(null, sqlFindAvailableBooks);
    }

    @Override
    public List<Book> findBooksBy(String title, String[] authors,
                                  Integer[] genreIds, String description) throws DaoException {
        String searchQuery = new JdbcBookSearchQueryBuilder(bookColumnNamesString, title, authors,
                genreIds, description).build();
        return findPreparedEntities(null, searchQuery);
    }
}
