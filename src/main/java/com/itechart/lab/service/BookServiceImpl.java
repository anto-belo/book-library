package com.itechart.lab.service;

import com.itechart.lab.connection.ConnectionManager;
import com.itechart.lab.dao.BookDao;
import com.itechart.lab.dao.EnumDao;
import com.itechart.lab.dao.EnumDaoFactory;
import com.itechart.lab.dto.BookInfo;
import com.itechart.lab.entity.Book;
import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.entity.IdPair;
import com.itechart.lab.exception.ConnectionManagerException;
import com.itechart.lab.exception.DaoException;
import com.itechart.lab.exception.ServiceException;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
public class BookServiceImpl implements BookService {
    private static final BookServiceImpl instance = new BookServiceImpl();

    private static final String MSG_FAILED_FIND_BOOK_BY_ID
            = "DAO failed to find book by id (#%d)";
    private static final String MSG_FAILED_FIND_BOOK_BY_ISBN
            = "DAO failed to find book by ISBN (%s)";
    private static final String MSG_FAILED_FIND_BOOKS_IN_RANGE
            = "DAO failed to find books in range (%d - %d)";
    private static final String MSG_FAILED_FIND_BOOK_AUTHORS
            = "DAO failed to find book authors (#%d)";
    private static final String MSG_FAILED_FIND_BOOK_GENRES
            = "DAO failed to find book genres (#%d)";
    private static final String MSG_FAILED_GET_ROWS_AMOUNT
            = "DAO failed to get rows amount";
    private static final String MSG_FAILED_GET_AVAILABLE_BOOKS_AMOUNT
            = "DAO failed to get available books amount";
    private static final String MSG_FAILED_DELETE_BY_IDS_RANGE
            = "DAO failed to delete by ids range (##%s)";
    private static final String MSG_CONN_MANAGER_NOT_RESPONDING
            = "Connection manager is not responding";
    private static final String MSG_FAILED_SAVE_BOOK_INFO
            = "DAOs failed to save book info";
    private static final String MSG_FAILED_FIND_FILTERED_BOOKS
            = "DAO failed to find filtered books";
    private static final String MSG_FAILED_FIND_OR_ADD_PUBLISHER
            = "DAO failed to find or add publisher (%s)";
    private static final String MSG_FAILED_SAVE_BOOK
            = "DAO failed to save book (%s)";
    private static final String MSG_FAILED_UPDATE_BOOK
            = "DAO failed to update book (%s)";
    private static final String MSG_FAILED_FIND_OR_ADD_ENTITIES
            = "DAO failed to find or add entities";
    private static final String MSG_FAILED_LINK_BOOK_TO_ENTITIES
            = "Failed to link book to entities";

    private BookServiceImpl() {
    }

    public static BookServiceImpl getInstance() {
        return instance;
    }

    @Override
    public Optional<Book> findById(int id) throws ServiceException {
        try {
            return BookDao.getInstance().findById(id);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BOOK_BY_ID, id));
        }
    }

    private Optional<Book> findByIsbn(String isbn) throws ServiceException {
        try {
            return BookDao.getInstance().findByIsbn(isbn);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BOOK_BY_ISBN, isbn));
        }
    }

    @Override
    public List<Book> findByPage(int perPage, int page,
                                 boolean availableOnly) throws ServiceException {
        int offset = perPage * (page - 1);
        try {
            return BookDao.getInstance()
                    .findBooksInRange(offset, perPage, availableOnly);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BOOKS_IN_RANGE,
                    offset, offset + perPage));
        }
    }

    @Override
    public List<EnumEntity> findAuthors(int bookId) throws ServiceException {
        try {
            List<Integer> authorIds = EnumDaoFactory.getBookAuthorDao()
                    .findRangeById(bookId).stream()
                    .map(IdPair::getId2)
                    .collect(Collectors.toList());
            return EnumDaoFactory.getAuthorDao().findByIdsRange(authorIds);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BOOK_AUTHORS, bookId));
        }
    }

    @Override
    public List<EnumEntity> findGenres(int bookId) throws ServiceException {
        try {
            List<Integer> genreIds = EnumDaoFactory.getBookGenreDao()
                    .findRangeById(bookId).stream()
                    .map(IdPair::getId2)
                    .collect(Collectors.toList());
            return EnumDaoFactory.getGenreDao().findByIdsRange(genreIds);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_BOOK_GENRES, bookId));
        }
    }

    @Override
    public int getBooksAmount() throws ServiceException {
        try {
            return BookDao.getInstance().getRowsAmount();
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_GET_ROWS_AMOUNT);
        }
    }

    @Override
    public int getAvailableBooksAmount() throws ServiceException {
        try {
            return BookDao.getInstance().getAvailableBooksAmount();
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_GET_AVAILABLE_BOOKS_AMOUNT);
        }
    }

    @Override
    public void deleteByIdsRange(List<Integer> ids) throws ServiceException {
        try {
            BookDao.getInstance().deleteByIdsRange(ids);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_DELETE_BY_IDS_RANGE,
                    Arrays.toString(ids.toArray(new Integer[0]))));
        }
    }

    @Override
    public void save(BookInfo bookInfo) throws ServiceException {
        Optional<Book> optDBBook = findByIsbn(bookInfo.getIsbn());
        BookInfo dbBookInfo = optDBBook.map(BookInfo::new).orElse(new BookInfo());
        ConnectionManager manager = ConnectionManager.getManager();
        try {
            manager.setAutoCommit(false);

            Integer bookId = dbBookInfo.getId();
            String coverUrl = bookInfo.getCoverUrl();
            coverUrl = coverUrl == null ? dbBookInfo.getCoverUrl() : coverUrl;
            int publisherId = getPublisherId(bookInfo.getPublisher());
            Book book = new Book(
                    bookId,
                    coverUrl,
                    bookInfo.getTitle(),
                    publisherId,
                    bookInfo.getPublishYear(),
                    bookInfo.getPageCount(),
                    bookInfo.getIsbn(),
                    bookInfo.getTotalAmount(),
                    bookInfo.getRemainingAmount(),
                    bookInfo.getDescription()
            );

            if (dbBookInfo.getId() != null) {
                update(book);
            } else {
                bookId = save(book);
            }

            bookInfo.setId(bookId);

            updateBookLinks(bookId, bookInfo.getGenres(), bookInfo.getAuthors());

            manager.commit();
            manager.setAutoCommit(true);
        } catch (ConnectionManagerException | ServiceException e) {
            log.error(e.getMessage());
            try {
                manager.rollback();
                manager.setAutoCommit(true);
            } catch (ConnectionManagerException ex) {
                log.fatal(ex.getMessage());
                ConnectionManager.getManager().destroy();
                throw new ServiceException(MSG_CONN_MANAGER_NOT_RESPONDING);
            }
            throw new ServiceException(MSG_FAILED_SAVE_BOOK_INFO);
        }
    }

    @Override
    public List<Book> search(String title, List<String> authors,
                             List<String> genres, String description) throws ServiceException {
        try {
            return BookDao.getInstance().findBooksBy(title, authors, genres, description);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_FIND_FILTERED_BOOKS);
        }
    }

    private int getPublisherId(String publisher) throws ServiceException {
        int publisherId;
        try {
            publisherId = EnumDaoFactory.getPublisherDao()
                    .findByValueOrAdd(new EnumEntity(publisher))
                    .getId();
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_FIND_OR_ADD_PUBLISHER, publisher));
        }
        return publisherId;
    }

    private int save(Book book) throws ServiceException {
        try {
            return BookDao.getInstance().save(book).getId();
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_SAVE_BOOK, book));
        }
    }

    private void update(Book book) throws ServiceException {
        try {
            BookDao.getInstance().update(book);
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(String.format(MSG_FAILED_UPDATE_BOOK, book));
        }
    }

    private void updateBookLinks(int bookId, String genresString,
                                 String authorsString) throws ServiceException {
        List<Integer> genreIds = getEntityIds(genresString, EnumDaoFactory.getGenreDao());
        linkBookToEntities(bookId, genreIds, EnumDaoFactory.getBookGenreDao());

        List<Integer> authorIds = getEntityIds(authorsString, EnumDaoFactory.getAuthorDao());
        linkBookToEntities(bookId, authorIds, EnumDaoFactory.getBookAuthorDao());
    }

    private List<Integer> getEntityIds(String entitiesString, EnumDao<EnumEntity,
            String> entityDao) throws ServiceException {
        List<Integer> ids = new ArrayList<>();
        try {
            for (String entity : entitiesString.split(", ")) {
                ids.add(entityDao.findByValueOrAdd(new EnumEntity(entity)).getId());
                ids = ids.stream().distinct().collect(Collectors.toList());
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_FIND_OR_ADD_ENTITIES);
        }
        return ids;
    }

    private void linkBookToEntities(int bookId, List<Integer> entityIds, EnumDao<IdPair,
            Integer> linkDao) throws ServiceException {
        try {
            linkDao.delete(bookId);
            for (int entityId : entityIds) {
                linkDao.save(new IdPair(bookId, entityId));
            }
        } catch (DaoException e) {
            log.error(e.getMessage());
            throw new ServiceException(MSG_FAILED_LINK_BOOK_TO_ENTITIES);
        }
    }
}
