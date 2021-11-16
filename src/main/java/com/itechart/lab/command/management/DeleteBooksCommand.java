package com.itechart.lab.command.management;

import com.itechart.lab.command.AppCommand;
import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.util.Parser;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class DeleteBooksCommand implements Command {
    private static final DeleteBooksCommand instance = new DeleteBooksCommand();

    private static final String SELECTED_BOOK_PARAMETER_NAME = "selectedBook";

    private DeleteBooksCommand() {
    }

    public static DeleteBooksCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        String[] selectedBooksParam = request.getParameterValues(SELECTED_BOOK_PARAMETER_NAME);
        if (selectedBooksParam != null) {
            List<Integer> selectedBooks = Parser.parseIntList(Arrays.asList(selectedBooksParam));
            try {
                BookService.getInstance().deleteByIdsRange(selectedBooks);
            } catch (ServiceException e) {
                log.error(e.getMessage());
                return CommandResponse.ERROR_500;
            }
        }
        return AppCommand.MAIN_PAGE.getCommand().execute(request);
    }
}
