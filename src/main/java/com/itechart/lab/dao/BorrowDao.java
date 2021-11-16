package com.itechart.lab.dao;

import com.itechart.lab.entity.Borrow;
import com.itechart.lab.exception.DaoException;

import java.util.List;

public interface BorrowDao extends Dao<Borrow> {
    static BorrowDao getInstance() {
        return JdbcBorrowDao.getInstance();
    }

    List<Borrow> findBorrows(int bookId) throws DaoException;

    List<Borrow> findUnclosedBorrows(String dueDate) throws DaoException;
}
