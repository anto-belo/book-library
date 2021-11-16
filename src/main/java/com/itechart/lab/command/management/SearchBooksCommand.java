package com.itechart.lab.command.management;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.dto.BookRecord;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SearchBooksCommand implements Command {
    private static final SearchBooksCommand instance = new SearchBooksCommand();

    private SearchBooksCommand() {
    }

    public static SearchBooksCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        String bookTitle = request.getParameter("book-title");
        String bookAuthorsString = request.getParameter("book-authors");
        String bookGenresString = request.getParameter("book-genres");
        String bookDescription = request.getParameter("book-description");

        if (bookTitle.isEmpty() && bookAuthorsString.isEmpty()
                && bookGenresString.isEmpty() && bookDescription.isEmpty()) {
            request.setAttribute("noFilter", true);
            return CommandResponse.SEARCH;
        }

        bookAuthorsString = bookAuthorsString.isEmpty() ? null : bookAuthorsString;
        List<String> bookAuthors = bookAuthorsString == null ? Collections.emptyList()
                : Arrays.asList(bookAuthorsString.split(", "));

        bookGenresString = bookGenresString.isEmpty() ? null : bookGenresString;
        List<String> bookGenres = bookGenresString == null ? Collections.emptyList()
                : Arrays.asList(bookGenresString.split(", "));

        List<BookRecord> foundBooksRecords;
        try {
            foundBooksRecords = BookService.getInstance()
                    .search(bookTitle, bookAuthors, bookGenres, bookDescription).stream()
                    .map(BookRecord::new)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            log.warn(e.getMessage());
            return CommandResponse.ERROR_500;
        }

        if (foundBooksRecords.isEmpty()) {
            request.setAttribute("nothingFound", true);
        } else {
            request.setAttribute("books", foundBooksRecords);
        }
        request.setAttribute("bookTitle", bookTitle);
        request.setAttribute("bookAuthors", bookAuthorsString);
        request.setAttribute("bookGenres", bookGenresString);
        request.setAttribute("bookDescription", bookDescription);
        return CommandResponse.SEARCH;
    }
}
