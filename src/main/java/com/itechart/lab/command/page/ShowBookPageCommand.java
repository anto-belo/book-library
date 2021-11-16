package com.itechart.lab.command.page;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.util.Parser;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Log4j2
public class ShowBookPageCommand implements Command {
    private static final ShowBookPageCommand instance = new ShowBookPageCommand();

    private static final String BOOK_ID_PARAMETER_NAME = "bookId";

    private ShowBookPageCommand() {
    }

    public static ShowBookPageCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        int bookId = resolveBookId(request);
        if (bookId <= -1) {
            return CommandResponse.ERROR_404;
        }
        return CommandResponse.BOOK;
    }

    private int resolveBookId(HttpServletRequest request) {
        String bookIdParam = request.getParameter(BOOK_ID_PARAMETER_NAME);
        if (bookIdParam == null) {
            return 0;
        }
        Optional<Integer> bookId = Parser.tryParseInt(bookIdParam);
        return bookId.orElse(-1);
    }
}
