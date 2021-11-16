package com.itechart.lab.service;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.exception.ServiceException;

import java.util.Optional;

public interface EnumEntityService<T extends DBEntity> {
    static EnumEntityService<EnumEntity> getPublisherService() {
        return PublisherServiceImpl.getInstance();
    }

    Optional<T> findById(int id) throws ServiceException;
}
