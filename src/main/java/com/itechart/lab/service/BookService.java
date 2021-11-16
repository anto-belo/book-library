package com.itechart.lab.service;

import com.itechart.lab.dto.BookInfo;
import com.itechart.lab.entity.Book;
import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public interface BookService {
    static BookService getInstance() {
        return BookServiceImpl.getInstance();
    }

    Optional<Book> findById(int id) throws ServiceException;

    List<Book> findByPage(int perPage, int page, boolean availableOnly) throws ServiceException;

    List<EnumEntity> findAuthors(int bookId) throws ServiceException;

    List<EnumEntity> findGenres(int bookId) throws ServiceException;

    int getBooksAmount() throws ServiceException;

    int getAvailableBooksAmount() throws ServiceException;

    void deleteByIdsRange(List<Integer> ids) throws ServiceException;

    void save(BookInfo bookInfo) throws ServiceException;

    List<Book> search(String title, List<String> authors,
                      List<String> genres, String description) throws ServiceException;
}
