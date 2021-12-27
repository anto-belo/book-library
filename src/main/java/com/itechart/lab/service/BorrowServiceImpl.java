package com.itechart.lab.service;

import com.itechart.lab.dao.BorrowDao;
import com.itechart.lab.dao.ReaderDao;
import com.itechart.lab.dto.BorrowRecord;
import com.itechart.lab.entity.Borrow;
import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
public class BorrowServiceImpl implements BorrowService {
    private static final BorrowServiceImpl instance = new BorrowServiceImpl();

    private static final String MSG_FAILED_FIND_BORROWS
            = "DAO failed to find borrows (#%d)";
    private static final String MSG_FAILED_UPDATE_BORROW
            = "DAO failed to update borrow (%s)";
    private static final String MSG_FAILED_SAVE_BORROW
            = "DAO failed to save borrow (%s)";
    private static final String MSG_FAILED_FIND_BORROWS_EXPIRING_ON
            = "DAO failed to find borrows expiring on %s";

    private BorrowServiceImpl() {
    }

    public static BorrowServiceImpl getInstance() {
        return instance;
    }

    @Override
    public List<Borrow> findBorrows(int bookId) throws ServiceException {
        try {
            return BorrowDao.getInstance().findBorrows(bookId);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BORROWS, bookId));
        }
    }

    @Override
    public void save(BorrowRecord record, int bookId) throws ServiceException {
        Integer borrowInDbId = record.getId();
        if (borrowInDbId != null) {
            try {
                Optional<Borrow> optBorrowInDb = BorrowDao.getInstance().findById(borrowInDbId);
                if (optBorrowInDb.isPresent()) {
                    Borrow borrowInDb = optBorrowInDb.get();
                    borrowInDb.setBorrowStatus(record.getStatusId());
                    borrowInDb.setReturnDate(Date.valueOf(record.getReturnDate()));
                    BorrowDao.getInstance().update(borrowInDb);
                    return;
                }
            } catch (DaoException e) {
                log.error(e.getMessage());
                throw new ServiceException(String.format(MSG_FAILED_UPDATE_BORROW, record));
            }
        }
        String borrowDateString = record.getBorrowDate();
        Date borrowDate = borrowDateString == null ? null : Date.valueOf(borrowDateString);
        String returnDateString = record.getReturnDate();
        Date returnDate = returnDateString.equals("[Not returned]")
                ? null : Date.valueOf(returnDateString);
        Borrow borrow = null;
        try {
            int readerId = ReaderDao.getInstance()
                    .findOrAdd(new Reader(record.getReaderName(), record.getReaderEmail()))
                    .getId();
            borrow = new Borrow(
                    readerId,
                    bookId,
                    borrowDate,
                    record.getForMonths(),
                    returnDate,
                    record.getComment(),
                    record.getStatusId()
            );
            BorrowDao.getInstance().save(borrow);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_SAVE_BORROW, borrow));
        }
    }

    @Override
    public List<Borrow> findBorrowsExpiringOn(String dueDate) throws ServiceException {
        try {
            return BorrowDao.getInstance().findBorrowsExpiringOn(dueDate);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(
                    String.format(MSG_FAILED_FIND_BORROWS_EXPIRING_ON, dueDate));
        }
    }
}
