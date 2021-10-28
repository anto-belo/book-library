package com.itechart.lab.dao;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;

import java.util.List;

public interface EnumDao<T extends DBEntity, V> {
    T findByValueOrAdd(T entity) throws DaoException;

    List<T> findByValuesRange(V[] values) throws DaoException;

    int getId(T entity);

    V getValue(T entity);
}
