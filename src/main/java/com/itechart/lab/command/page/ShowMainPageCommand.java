package com.itechart.lab.command.page;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.dto.BookRecord;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BookService;
import com.itechart.lab.util.Parser;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ShowMainPageCommand implements Command {
    private static final ShowMainPageCommand instance = new ShowMainPageCommand();

    private static final String PAGE_NUMBER_PARAMETER = "page";
    private static final int FIRST_PAGE_NUMBER = 1;

    private static final String AVAILABILITY_PARAMETER = "availability";
    private static final int AVAILABILITY_ALL = 0;
    private static final int AVAILABILITY_AVAILABLE_ONLY = 1;

    private static final String PER_PAGE_PARAMETER = "perPage";
    private static final int SHORT_PER_PAGE_LIMIT = 10;
    private static final int LONG_PER_PAGE_LIMIT = 20;

    private ShowMainPageCommand() {
    }

    public static ShowMainPageCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        int page = getPage(request);
        int perPage = getPerPage(request);
        int availability = getAvailability(request);

        boolean availableOnly = availability == AVAILABILITY_AVAILABLE_ONLY;

        int lastPage = getLastPage(perPage, availableOnly);
        if (lastPage == -1) {
            return CommandResponse.ERROR_500;
        }
        page = Math.min(page, lastPage);

        List<BookRecord> bookRecords;
        try {
            bookRecords = BookService.getInstance().findByPage(perPage, page, availableOnly)
                    .stream()
                    .map(BookRecord::new)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            log.error(e.getMessage());
            return CommandResponse.ERROR_500;
        }

        request.setAttribute("books", bookRecords);
        request.setAttribute("currentPage", page);
        request.setAttribute("lastPage", lastPage);
        request.setAttribute("perPage", perPage);
        request.setAttribute("availability", availability);
        return CommandResponse.MAIN;
    }

    private int getPage(HttpServletRequest request) {
        String pageParam = request.getParameter(PAGE_NUMBER_PARAMETER);
        int page = Parser.tryParseInt(pageParam).orElse(FIRST_PAGE_NUMBER);
        return Math.max(page, FIRST_PAGE_NUMBER);
    }

    private int getPerPage(HttpServletRequest request) {
        String perPageParam = request.getParameter(PER_PAGE_PARAMETER);
        int perPage = Parser.tryParseInt(perPageParam).orElse(SHORT_PER_PAGE_LIMIT);
        return perPage == LONG_PER_PAGE_LIMIT ? perPage : SHORT_PER_PAGE_LIMIT;
    }

    private int getAvailability(HttpServletRequest request) {
        String availabilityParam = request.getParameter(AVAILABILITY_PARAMETER);
        int availability = Parser.tryParseInt(availabilityParam).orElse(AVAILABILITY_ALL);
        return availability == AVAILABILITY_AVAILABLE_ONLY ? availability : AVAILABILITY_ALL;
    }

    private int getLastPage(int perPage, boolean availableOnly) {
        int lastPage;
        try {
            int totalAmount = availableOnly ? BookService.getInstance().getAvailableBooksAmount()
                    : BookService.getInstance().getBooksAmount();
            lastPage = (int) Math.ceil((double) totalAmount / perPage);
        } catch (ServiceException e) {
            log.warn(e.getMessage());
            lastPage = -1;
        }
        return lastPage;
    }
}
