package com.itechart.lab.command.ajax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.lab.command.Command;
import com.itechart.lab.dto.BookInfo;
import com.itechart.lab.entity.Book;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.itechart.lab.command.CommandResponse.MSG_FAILED_SEND_JSON;
import static com.itechart.lab.command.CommandResponse.MSG_FAILED_SERIALIZE_TO_JSON;
import static com.itechart.lab.command.CommandResponse.sendJson;

@Log4j2
public class GetBookInfoCommand implements Command {
    private static final GetBookInfoCommand instance = new GetBookInfoCommand();

    private static final String BOOK_ID_PARAMETER_NAME = "bookId";

    private static final String JSON_REDIRECT = "{'redirect': '%s'}";
    private static final String ERROR_500_PAGE = "/controller?command=error_500_page";
    private static final String ERROR_404_PAGE = "/controller?command=error_404_page";

    private static final String MSG_FAILED_FIND_BOOK = "Failed to find book by id (#%s)";

    private GetBookInfoCommand() {
    }

    public static GetBookInfoCommand getInstance() {
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        int bookId = Integer.parseInt(request.getParameter(BOOK_ID_PARAMETER_NAME));
        BookInfo bookInfo;
        try {
            Optional<Book> optCurrentBook;
            optCurrentBook = BookService.getInstance().findById(bookId);
            if (!optCurrentBook.isPresent()) {
                if (!sendJson(response, String.format(JSON_REDIRECT, ERROR_404_PAGE))) {
                    log.error(MSG_FAILED_SEND_JSON);
                }
                return;
            }
            bookInfo = new BookInfo(optCurrentBook.get());
            ObjectMapper mapper = new ObjectMapper();
            String bookInfoJson = null;
            try {
                bookInfoJson = mapper.writeValueAsString(bookInfo);
            } catch (JsonProcessingException e) {
                log.error(MSG_FAILED_SERIALIZE_TO_JSON);
            }
            if (!sendJson(response, bookInfoJson)) {
                log.error(MSG_FAILED_SEND_JSON);
            }
        } catch (ServiceException e) {
            log.error(String.format(MSG_FAILED_FIND_BOOK, bookId));
            if (!sendJson(response, String.format(JSON_REDIRECT, ERROR_500_PAGE))) {
                log.error(MSG_FAILED_SEND_JSON);
            }
        }
    }
}
