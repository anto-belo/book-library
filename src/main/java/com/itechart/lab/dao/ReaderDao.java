package com.itechart.lab.dao;

import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface ReaderDao {
    List<Reader> findReadersByEmailLike(String email) throws DaoException;

    Optional<Reader> findReaderByEmail(String email) throws DaoException;
}
