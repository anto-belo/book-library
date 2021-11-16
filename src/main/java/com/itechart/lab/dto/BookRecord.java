package com.itechart.lab.dto;

import com.itechart.lab.entity.Book;
import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.util.RangeFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Log4j2
public class BookRecord {
    private static final String STUB_UNABLE_FIND_AUTHORS = "[UNABLE TO FIND AUTHORS]";

    protected Integer id;
    protected String title;
    protected String authors;
    protected int publishYear;
    protected int remainingAmount;

    public BookRecord(Book book) {
        id = book.getId();
        title = book.getTitle();
        authors = RangeFormatter.format(getAuthors(book.getId()), false);
        publishYear = book.getPublishYear();
        remainingAmount = book.getRemainingAmount();
    }

    private List<String> getAuthors(int bookId) {
        List<String> bookAuthors;
        try {
            bookAuthors = BookService.getInstance()
                    .findAuthors(bookId)
                    .stream()
                    .map(EnumEntity::getValue)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            log.error(e.getMessage());
            bookAuthors = new ArrayList<>();
            bookAuthors.add(STUB_UNABLE_FIND_AUTHORS);
        }
        return bookAuthors;
    }
}
