package com.itechart.lab.dao;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;

import java.util.List;

public interface EnumDao<T extends DBEntity, V> extends Dao<T> {
    List<T> findRangeById(int id) throws DaoException;

    List<T> findByIdsRange(List<Integer> ids) throws DaoException;

    T findByValueOrAdd(T entity) throws DaoException;

    int getId(T entity);

    V getValue(T entity);
}
