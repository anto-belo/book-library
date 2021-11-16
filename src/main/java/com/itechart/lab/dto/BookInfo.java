package com.itechart.lab.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.lab.entity.Book;
import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.service.BorrowService;
import com.itechart.lab.service.EnumEntityService;
import com.itechart.lab.util.RangeFormatter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@Log4j2
public class BookInfo extends BookRecord {
    private static final String FORM_FIELD_ID = "id";
    private static final String FORM_FIELD_BOOK_COVER = "book-cover";
    private static final String FORM_FIELD_BOOK_TITLE = "book-title";
    private static final String FORM_FIELD_BOOK_AUTHORS = "book-authors";
    private static final String FORM_FIELD_BOOK_PUBLISHER = "book-publisher";
    private static final String FORM_FIELD_BOOK_PUBLISH_YEAR = "book-publish-year";
    private static final String FORM_FIELD_BOOK_GENRES = "book-genres";
    private static final String FORM_FIELD_BOOK_PAGE_COUNT = "book-page-count";
    private static final String FORM_FIELD_BOOK_ISBN = "book-isbn";
    private static final String FORM_FIELD_BOOK_TOTAL_AMOUNT = "book-total-amount";
    private static final String FORM_FIELD_BOOK_REMAINING_AMOUNT = "book-remaining-amount";
    private static final String FORM_FIELD_BOOK_DESCRIPTION = "book-description";
    private static final String FORM_FIELD_BORROW = "borrow";

    private static final String STUB_UNABLE_FIND_PUBLISHER = "[UNABLE TO FIND PUBLISHER]";
    private static final String STUB_UNABLE_FIND_GENRES = "[UNABLE TO FIND GENRES]";
    private static final String STUB_UNABLE_RESOLVE_AVAILABLE_DATE
            = "[UNABLE TO RESOLVE AVAILABLE DATE]";

    private String coverUrl;
    private String publisher;
    private String genres;
    private int pageCount;
    private String isbn;
    private int totalAmount;
    private String description;
    private List<BorrowRecord> borrows;

    public BookInfo() {
        borrows = new ArrayList<>();
    }

    public BookInfo(Book book) {
        super(book);
        coverUrl = book.getCoverUrl();
        publisher = getPublisher(book.getPublisherId());
        genres = RangeFormatter.format(getGenres(book.getId()), false);
        pageCount = book.getPageCount();
        isbn = book.getIsbn();
        totalAmount = book.getTotalAmount();
        description = book.getDescription();
        borrows = getBorrows(book.getId());
    }

    private String getPublisher(int publisherId) {
        String publisher;
        try {
            publisher = EnumEntityService.getPublisherService()
                    .findById(publisherId)
                    .map(EnumEntity::getValue)
                    .orElse(STUB_UNABLE_FIND_PUBLISHER);
        } catch (ServiceException e) {
            log.error(e.getMessage());
            publisher = STUB_UNABLE_FIND_PUBLISHER;
        }
        return publisher;
    }

    private List<String> getGenres(int bookId) {
        List<String> bookGenres;
        try {
            bookGenres = BookService.getInstance()
                    .findGenres(bookId)
                    .stream()
                    .map(EnumEntity::getValue)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            log.error(e.getMessage());
            bookGenres = new ArrayList<>();
            bookGenres.add(STUB_UNABLE_FIND_GENRES);
        }
        return bookGenres;
    }

    private List<BorrowRecord> getBorrows(int bookId) {
        List<BorrowRecord> borrowRecords = null;
        try {
            borrowRecords = BorrowService.getInstance().findBorrows(bookId).stream()
                    .map(BorrowRecord::new)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
        return borrowRecords;
    }

    public void setFormField(String fieldName, String value) {
        switch (fieldName) {
            case FORM_FIELD_ID:
                id = Integer.parseInt(value);
                break;
            case FORM_FIELD_BOOK_COVER:
                coverUrl = value;
                break;
            case FORM_FIELD_BOOK_TITLE:
                title = value;
                break;
            case FORM_FIELD_BOOK_AUTHORS:
                authors = value;
                break;
            case FORM_FIELD_BOOK_PUBLISHER:
                publisher = value;
                break;
            case FORM_FIELD_BOOK_PUBLISH_YEAR:
                publishYear = Integer.parseInt(value);
                break;
            case FORM_FIELD_BOOK_GENRES:
                genres = value;
                break;
            case FORM_FIELD_BOOK_PAGE_COUNT:
                pageCount = Integer.parseInt(value);
                break;
            case FORM_FIELD_BOOK_ISBN:
                isbn = value;
                break;
            case FORM_FIELD_BOOK_TOTAL_AMOUNT:
                totalAmount = Integer.parseInt(value);
                break;
            case FORM_FIELD_BOOK_REMAINING_AMOUNT:
                remainingAmount = Integer.parseInt(value);
                break;
            case FORM_FIELD_BOOK_DESCRIPTION:
                description = value.trim().isEmpty() ? null : value.trim();
                break;
            case FORM_FIELD_BORROW:
                ObjectMapper mapper = new ObjectMapper();
                BorrowRecord record;
                try {
                    record = mapper.readValue(value, BorrowRecord.class);
                    borrows.add(record);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
