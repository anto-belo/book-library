package com.itechart.lab.command.page;

import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;

import javax.servlet.http.HttpServletRequest;

public class ShowError500PageCommand implements Command {
    private static final ShowError500PageCommand instance = new ShowError500PageCommand();

    private ShowError500PageCommand() {
    }

    public static ShowError500PageCommand getInstance() {
        return instance;
    }

    @Override
    public CommandResponse execute(HttpServletRequest request) {
        return CommandResponse.ERROR_500;
    }
}
