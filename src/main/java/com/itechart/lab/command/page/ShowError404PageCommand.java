package com.itechart.lab.command.page;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;

import javax.servlet.http.HttpServletRequest;

public class ShowError404PageCommand implements Command {
    private static final ShowError404PageCommand instance = new ShowError404PageCommand();

    private ShowError404PageCommand() {
    }

    public static ShowError404PageCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        return CommandResponse.ERROR_404;
    }
}
