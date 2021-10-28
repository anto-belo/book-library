package com.itechart.lab.dao;

import static com.itechart.lab.dao.AbstractDao.SQL_TEMPLATE_FIND_ALL;
import static com.itechart.lab.dao.AbstractDao.SQL_TEMPLATE_IN;
import static com.itechart.lab.dao.AbstractDao.SQL_TEMPLATE_SUB_QUERY_IN;

public class JdbcBookSearchQueryBuilder {
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String LIKE_CLAUSE = "%s LIKE '%%%s%%'";
    private static final String IN_CLAUSE = "%s IN (%s)";
    private static final String AND_WORD = " AND ";

    private final String bookColumnNamesString;
    private final String title;
    private final String[] authors;
    private final Integer[] genreIds;
    private final String description;
    private StringBuilder query;

    public JdbcBookSearchQueryBuilder(String bookColumnNamesString, String title, String[] authors,
                                      Integer[] genreIds, String description) {
        this.bookColumnNamesString = bookColumnNamesString;
        this.title = title;
        this.authors = authors;
        this.genreIds = genreIds;
        this.description = description;
    }

    public String build() {
        query = new StringBuilder(String.format(SQL_TEMPLATE_FIND_ALL,
                bookColumnNamesString, JdbcBookDao.TABLE_NAME) + WHERE_CLAUSE);
        boolean anyParams = false;
        if (title != null) {
            query.append(String.format(LIKE_CLAUSE, JdbcBookDao.COLUMN_TITLE, title));
            anyParams = true;
        }
        if (authors != null) {
            if (anyParams) {
                query.append(AND_WORD);
            }
            anyParams = true;
            addAuthorsToQuery();
        }
        if (genreIds != null) {
            if (anyParams) {
                query.append(AND_WORD);
            }
            anyParams = true;
            addGenresToQuery();
        }
        if (description != null) {
            if (anyParams) {
                query.append(AND_WORD);
            }
            query.append(String.format(LIKE_CLAUSE, JdbcBookDao.COLUMN_BOOK_DESCRIPTION, description));
        }
        return query.toString();
    }

    private void addGenresToQuery() {
        String selectBookIds = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_SUB_QUERY_IN,
                EnumDaoFactory.BOOK_GENRE_COLUMN_BOOK_ID, EnumDaoFactory.BOOK_GENRE_TABLE_NAME,
                EnumDaoFactory.BOOK_GENRE_COLUMN_GENRE_ID, getIntegersEnumerationString(genreIds));
        query.append(String.format(IN_CLAUSE, JdbcBookDao.COLUMN_ID, selectBookIds));
    }

    private void addAuthorsToQuery() {
        String selectAuthorIds = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_IN,
                EnumDaoFactory.AUTHOR_COLUMN_ID, EnumDaoFactory.AUTHOR_TABLE_NAME, EnumDaoFactory.AUTHOR_COLUMN_NAME);
        selectAuthorIds = selectAuthorIds.replace("?", getStringsEnumerationString(authors));
        String selectBookIds = String.format(SQL_TEMPLATE_FIND_ALL + SQL_TEMPLATE_SUB_QUERY_IN,
                EnumDaoFactory.BOOK_AUTHOR_COLUMN_BOOK_ID, EnumDaoFactory.BOOK_AUTHOR_TABLE_NAME,
                EnumDaoFactory.BOOK_AUTHOR_COLUMN_AUTHOR_ID, selectAuthorIds);
        query.append(String.format(IN_CLAUSE, JdbcBookDao.COLUMN_ID, selectBookIds));
    }

    private String getStringsEnumerationString(String[] strings) {
        if (strings.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("'" + strings[0] + "'");
        for (int i = 1; i < strings.length; i++) {
            sb.append(", '").append(strings[i]).append("'");
        }
        return sb.toString();
    }

    private String getIntegersEnumerationString(Integer[] integers) {
        if (integers.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(integers[0] + "");
        for (int i = 1; i < integers.length; i++) {
            sb.append(", ").append(integers[i]);
        }
        return sb.toString();
    }
}
