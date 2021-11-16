package com.itechart.lab.command.page;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;

import javax.servlet.http.HttpServletRequest;

public class ShowSearchPageCommand implements Command {
    private static final ShowSearchPageCommand instance = new ShowSearchPageCommand();

    private ShowSearchPageCommand() {
    }

    public static ShowSearchPageCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        return CommandResponse.SEARCH;
    }
}
