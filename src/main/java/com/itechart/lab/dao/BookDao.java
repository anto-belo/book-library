package com.itechart.lab.dao;

import com.itechart.lab.entity.Book;
import com.itechart.lab.exception.DaoException;

import java.util.List;

public interface BookDao {
    List<Book> findAvailableBooks() throws DaoException;

    List<Book> findBooksBy(String title, String[] authors, Integer[] genreIds, String description)
            throws DaoException;
}
