package com.itechart.lab.service;

import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface ReaderService {
    static ReaderService getInstance() {
        return ReaderServiceImpl.getInstance();
    }

    Optional<Reader> findById(int id) throws ServiceException;

    List<Reader> findReaderByEmailLike(String email) throws ServiceException;
}
