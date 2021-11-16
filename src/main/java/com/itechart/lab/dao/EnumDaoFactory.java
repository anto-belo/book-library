package com.itechart.lab.dao;

import com.itechart.lab.entity.EnumEntity;
import com.itechart.lab.entity.IdPair;

public class EnumDaoFactory {
    static final String AUTHOR_TABLE_NAME = "author";
    static final String AUTHOR_COLUMN_ID = "id";
    static final String AUTHOR_COLUMN_NAME = "a_name";

    static final String GENRE_TABLE_NAME = "genre";
    static final String GENRE_COLUMN_ID = "id";
    static final String GENRE_COLUMN_NAME = "g_name";

    static final String PUBLISHER_TABLE_NAME = "publisher";
    static final String PUBLISHER_COLUMN_ID = "id";
    static final String PUBLISHER_COLUMN_NAME = "p_name";

    static final String BOOK_AUTHOR_TABLE_NAME = "book_author";
    static final String BOOK_AUTHOR_COLUMN_BOOK_ID = "book_id";
    static final String BOOK_AUTHOR_COLUMN_AUTHOR_ID = "author_id";

    static final String BOOK_GENRE_TABLE_NAME = "book_genre";
    static final String BOOK_GENRE_COLUMN_BOOK_ID = "book_id";
    static final String BOOK_GENRE_COLUMN_GENRE_ID = "genre_id";

    private static final EnumDao<EnumEntity, String> AUTHOR_DAO = new JdbcEnumDao(
            AUTHOR_TABLE_NAME,
            AUTHOR_COLUMN_ID,
            AUTHOR_COLUMN_NAME
    );

    private static final EnumDao<EnumEntity, String> GENRE_DAO = new JdbcEnumDao(
            GENRE_TABLE_NAME,
            GENRE_COLUMN_ID,
            GENRE_COLUMN_NAME
    );

    private static final EnumDao<EnumEntity, String> PUBLISHER_DAO = new JdbcEnumDao(
            PUBLISHER_TABLE_NAME,
            PUBLISHER_COLUMN_ID,
            PUBLISHER_COLUMN_NAME
    );

    private static final EnumDao<IdPair, Integer> BOOK_AUTHOR_DAO = new JdbcIdPairDao(
            BOOK_AUTHOR_TABLE_NAME,
            BOOK_AUTHOR_COLUMN_BOOK_ID,
            BOOK_AUTHOR_COLUMN_AUTHOR_ID
    );

    private static final EnumDao<IdPair, Integer> BOOK_GENRE_DAO = new JdbcIdPairDao(
            BOOK_GENRE_TABLE_NAME,
            BOOK_GENRE_COLUMN_BOOK_ID,
            BOOK_GENRE_COLUMN_GENRE_ID
    );

    public static EnumDao<EnumEntity, String> getAuthorDao() {
        return AUTHOR_DAO;
    }

    public static EnumDao<EnumEntity, String> getGenreDao() {
        return GENRE_DAO;
    }

    public static EnumDao<EnumEntity, String> getPublisherDao() {
        return PUBLISHER_DAO;
    }

    public static EnumDao<IdPair, Integer> getBookAuthorDao() {
        return BOOK_AUTHOR_DAO;
    }

    public static EnumDao<IdPair, Integer> getBookGenreDao() {
        return BOOK_GENRE_DAO;
    }
}
