package com.itechart.lab.dao;

import com.itechart.lab.entity.Book;
import com.itechart.lab.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface BookDao extends Dao<Book> {
    static BookDao getInstance() {
        return JdbcBookDao.getInstance();
    }

    Optional<Book> findByIsbn(String isbn) throws DaoException;

    List<Book> findBooksInRange(int offset, int amount, boolean availableOnly) throws DaoException;

    int getAvailableBooksAmount() throws DaoException;

    List<Book> findBooksBy(String title, List<String> authors, List<String> genres,
                           String description) throws DaoException;
}
