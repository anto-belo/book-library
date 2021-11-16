package com.itechart.lab.dao;

import com.itechart.lab.entity.DBEntity;
import com.itechart.lab.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface Dao<T extends DBEntity> {
    T save(T entity) throws DaoException;

    List<T> findAll() throws DaoException;

    Optional<T> findById(int id) throws DaoException;

    void update(T entity) throws DaoException;

    void delete(int id) throws DaoException;

    void deleteByIdsRange(List<Integer> ids) throws DaoException;

    int getRowsAmount() throws DaoException;
}
