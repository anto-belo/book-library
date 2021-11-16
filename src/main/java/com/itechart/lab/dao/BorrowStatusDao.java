package com.itechart.lab.dao;

public interface BorrowStatusDao {
    static BorrowStatusDao getInstance() {
        return JdbcBorrowStatusDao.getInstance();
    }

    String getStatusValue(int status);

    int getValueStatus(String value);
}
