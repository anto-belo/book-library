package com.itechart.lab.service;

import com.itechart.lab.dao.EnumDao;
import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public abstract class AbstractEnumEntityService<T extends DBEntity, V>
        implements EnumEntityService<T> {

    private static final String MSG_FAILED_FIND_BY_ID = "DAO failed to find by id (#%d)";

    private final EnumDao<T, V> dao;

    protected AbstractEnumEntityService(EnumDao<T, V> dao) {
        this.dao = dao;
    }

    @Override
    public Optional<T> findById(int id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BY_ID, id));
        }
    }
}
