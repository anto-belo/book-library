package com.itechart.lab.dao;

import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.exception.DaoException;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class JdbcBorrowStatusDao extends JdbcEnumDao implements BorrowStatusDao {
    static final String TABLE_NAME = "borrow_status";
    static final String COLUMN_ID = "id";
    static final String COLUMN_VALUE = "bs_value";

    private static final JdbcBorrowStatusDao instance = new JdbcBorrowStatusDao();

    private static final String STATUS_RETURNED = "returned";
    private static final String STATUS_RETURNED_AND_DAMAGED = "returned and damaged";
    private static final String STATUS_LOST = "lost";

    private final Map<Integer, String> statusMap = new HashMap<>();

    private JdbcBorrowStatusDao() {
        super(TABLE_NAME, COLUMN_ID, COLUMN_VALUE);
        try {
            for (EnumEntity status : findAll()) {
                statusMap.put(status.getId(), status.getValue());
            }
        } catch (DaoException e) {
            log.warn(e.getMessage());
            useDefaultStatusMap();
        }
    }

    public static JdbcBorrowStatusDao getInstance() {
        return instance;
    }

    @Override
    public String getStatusValue(int status) {
        return statusMap.get(status);
    }

    @Override
    public int getValueStatus(String value) {
        return statusMap.entrySet().stream()
                .filter(e -> e.getValue().equals(value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    private void useDefaultStatusMap() {
        statusMap.put(1, STATUS_RETURNED);
        statusMap.put(2, STATUS_RETURNED_AND_DAMAGED);
        statusMap.put(3, STATUS_LOST);
    }

    @Override
    public EnumEntity save(EnumEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(EnumEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteByIdsRange(List<Integer> ids) {
        throw new UnsupportedOperationException();
    }
}
