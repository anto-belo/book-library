package com.itechart.lab.service;

import com.itechart.lab.dao.ReaderDao;
import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;

@Log4j2
public class ReaderServiceImpl implements ReaderService {
    private static final ReaderServiceImpl instance = new ReaderServiceImpl();

    private static final String MSG_FAILED_FIND_AUTHOR_BY_ID
            = "DAO failed to find author by id";
    private static final String MSG_FAILED_FIND_READERS_BY_EMAIL_LIKE
            = "DAO failed to find readers by email like (%s)";

    private ReaderServiceImpl() {
    }

    public static ReaderServiceImpl getInstance() {
        return instance;
    }

    @Override
    public Optional<Reader> findById(int id) throws ServiceException {
        try {
            return ReaderDao.getInstance().findById(id);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_FIND_AUTHOR_BY_ID);
        }
    }

    @Override
    public List<Reader> findReaderByEmailLike(String email) throws ServiceException {
        try {
            return ReaderDao.getInstance().findReadersByEmailLike(email);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(
                    String.format(MSG_FAILED_FIND_READERS_BY_EMAIL_LIKE, email));
        }
    }
}
