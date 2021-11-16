package com.itechart.lab.service;

import com.itechart.lab.dto.BorrowRecord;
import com.itechart.lab.entity.Borrow;
import com.itechart.lab.exception.ServiceException;

import java.util.List;

public interface BorrowService {
    static BorrowService getInstance() {
        return BorrowServiceImpl.getInstance();
    }

    List<Borrow> findBorrows(int bookId) throws ServiceException;

    void save(BorrowRecord record, int bookId) throws ServiceException;

    List<Borrow> findUnclosedBorrows(String dueDate) throws ServiceException;
}
