package com.itechart.lab.dto;

import com.itechart.lab.entity.Borrow;
import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.ReaderService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Data
@Log4j2
@NoArgsConstructor
public class BorrowRecord {
    private static final String STUB_UNABLE_FIND_READER = "[UNABLE TO FIND READER]";
    private static final String STUB_NOT_RETURNED = "[Not returned]";

    private Integer id;
    private String readerName;
    private String readerEmail;
    private String borrowDate;
    private int forMonths;
    private String dueDate;
    private String returnDate;
    private String comment;
    private Integer statusId;

    public BorrowRecord(Borrow borrow) {
        id = borrow.getId();
        Reader reader = getReader(borrow.getReaderId());
        if (reader == null) {
            readerName = STUB_UNABLE_FIND_READER;
            readerEmail = STUB_UNABLE_FIND_READER;
        } else {
            readerName = reader.getName();
            readerEmail = reader.getEmail();
        }
        LocalDate borrowDateLocal = borrow.getBorrowDate().toLocalDate();
        borrowDate = borrowDateLocal.toString();
        forMonths = borrow.getForMonths();
        dueDate = borrowDateLocal.plusMonths(borrow.getForMonths()).toString();
        Date returnDate = borrow.getReturnDate();
        this.returnDate = returnDate == null ? STUB_NOT_RETURNED : returnDate.toString();
        comment = borrow.getComment();
        statusId = borrow.getBorrowStatus();
    }

    private Reader getReader(int readerId) {
        Reader reader = null;
        try {
            Optional<Reader> optReader = ReaderService.getInstance().findById(readerId);
            if (optReader.isPresent()) {
                reader = optReader.get();
            }
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
        return reader;
    }
}
