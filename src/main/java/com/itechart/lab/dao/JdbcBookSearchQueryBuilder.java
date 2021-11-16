package com.itechart.lab.dao;

import com.itechart.lab.util.RangeFormatter;

import java.util.List;

import static com.itechart.lab.dao.AbstractDao.SQL_SELECT;
import static com.itechart.lab.dao.AbstractDao.SQL_SELECT_IN;

public class JdbcBookSearchQueryBuilder {
    private static final String SQL_WHERE_LIKE = " WHERE %s LIKE %s";
    private static final String SQL_AND_LIKE = " AND %s LIKE %s";
    private static final String SQL_WHERE_IN = " WHERE %s IN (%s)";
    private static final String SQL_AND_IN = " AND %s IN (%s)";

    private final String bookColumnNamesString;
    private final String title;
    private final List<String> authors;
    private final List<String> genres;
    private final String description;
    private StringBuilder query;
    private boolean anyParams;

    public JdbcBookSearchQueryBuilder(String bookColumnNamesString, String title,
                                      List<String> authors, List<String> genres,
                                      String description) {
        this.bookColumnNamesString = bookColumnNamesString;
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.description = description;
    }

    public String build() {
        query = new StringBuilder(String.format(SQL_SELECT,
                bookColumnNamesString, JdbcBookDao.TABLE_NAME));
        if (title != null && !title.isEmpty()) {
            query.append(String.format(SQL_WHERE_LIKE,
                    JdbcBookDao.COLUMN_TITLE, "'%" + title + "%'"));
            anyParams = true;
        }
        if (authors != null && authors.size() != 0) {
            addAuthorsToQuery();
            anyParams = true;
        }
        if (genres != null && genres.size() != 0) {
            addGenresToQuery();
            anyParams = true;
        }
        if (description != null && !description.isEmpty()) {
            if (anyParams) {
                query.append(String.format(SQL_AND_LIKE,
                        JdbcBookDao.COLUMN_BOOK_DESCRIPTION, "'%" + description + "%'"));
            } else {
                query.append(String.format(SQL_WHERE_LIKE,
                        JdbcBookDao.COLUMN_BOOK_DESCRIPTION, "'%" + description + "%'"));
            }
            anyParams = true;
        }
        return anyParams ? query.toString() : null;
    }

    private void addAuthorsToQuery() {
        String authorNamesString = RangeFormatter.format(authors, true);
        String selectAuthorIds = String.format(SQL_SELECT_IN,
                EnumDaoFactory.AUTHOR_COLUMN_ID, EnumDaoFactory.AUTHOR_TABLE_NAME,
                EnumDaoFactory.AUTHOR_COLUMN_NAME, authorNamesString);
        String selectBookIds = String.format(SQL_SELECT_IN,
                EnumDaoFactory.BOOK_AUTHOR_COLUMN_BOOK_ID, EnumDaoFactory.BOOK_AUTHOR_TABLE_NAME,
                EnumDaoFactory.BOOK_AUTHOR_COLUMN_AUTHOR_ID, selectAuthorIds);
        if (anyParams) {
            query.append(String.format(SQL_AND_IN, JdbcBookDao.COLUMN_ID, selectBookIds));
        } else {
            query.append(String.format(SQL_WHERE_IN, JdbcBookDao.COLUMN_ID, selectBookIds));
        }
    }

    private void addGenresToQuery() {
        String genreNamesString = RangeFormatter.format(genres, true);
        String selectGenreIds = String.format(SQL_SELECT_IN,
                EnumDaoFactory.GENRE_COLUMN_ID, EnumDaoFactory.GENRE_TABLE_NAME,
                EnumDaoFactory.GENRE_COLUMN_NAME, genreNamesString);
        String selectBookIds = String.format(SQL_SELECT_IN,
                EnumDaoFactory.BOOK_GENRE_COLUMN_BOOK_ID, EnumDaoFactory.BOOK_GENRE_TABLE_NAME,
                EnumDaoFactory.BOOK_GENRE_COLUMN_GENRE_ID, selectGenreIds);
        if (anyParams) {
            query.append(String.format(SQL_AND_IN, JdbcBookDao.COLUMN_ID, selectBookIds));
        } else {
            query.append(String.format(SQL_WHERE_IN, JdbcBookDao.COLUMN_ID, selectBookIds));
        }
    }
}
