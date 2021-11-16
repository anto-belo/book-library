package com.itechart.lab.dto;

import com.itechart.lab.entity.Book;
import com.itechart.lab.entity.Borrow;
import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.service.ReaderService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Data
@Log4j2
public class MailBorrow {
    private String readerName;
    private String readerEmail;
    private String bookTitle;

    public MailBorrow(Borrow borrow) {
        try {
            Optional<Reader> optReader = ReaderService.getInstance().findById(borrow.getReaderId());
            if (optReader.isPresent()) {
                readerName = optReader.get().getName();
                readerEmail = optReader.get().getEmail();
            }
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
        try {
            Optional<Book> optBook = BookService.getInstance().findById(borrow.getBookId());
            optBook.ifPresent(book -> bookTitle = book.getTitle());
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
    }
}
