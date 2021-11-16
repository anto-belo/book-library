package com.itechart.lab.command;

import com.itechart.lab.command.ajax.FindReadersByEmailLikeCommand;
import com.itechart.lab.command.ajax.GetBookInfoCommand;
import com.itechart.lab.command.management.DeleteBooksCommand;
import com.itechart.lab.command.management.SearchBooksCommand;
import com.itechart.lab.command.management.UpdateBookCommand;
import com.itechart.lab.command.page.ShowBookPageCommand;
import com.itechart.lab.command.page.ShowError404PageCommand;
import com.itechart.lab.command.page.ShowError500PageCommand;
import com.itechart.lab.command.page.ShowMainPageCommand;
import com.itechart.lab.command.page.ShowSearchPageCommand;

public enum AppCommand {
    MAIN_PAGE(ShowMainPageCommand.getInstance(), false),
    DELETE_BOOKS(DeleteBooksCommand.getInstance(), false),

    BOOK_PAGE(ShowBookPageCommand.getInstance(), false),
    UPDATE_BOOK(UpdateBookCommand.getInstance(), false),
    GET_READER(FindReadersByEmailLikeCommand.getInstance(), true),
    GET_BOOK(GetBookInfoCommand.getInstance(), true),

    SEARCH_PAGE(ShowSearchPageCommand.getInstance(), false),
    SEARCH(SearchBooksCommand.getInstance(),false),

    ERROR_500_PAGE(ShowError500PageCommand.getInstance(), false),
    ERROR_404_PAGE(ShowError404PageCommand.getInstance(), false),

    DEFAULT(ShowMainPageCommand.getInstance(), false);

    private final Command command;
    private final boolean ajax;

    AppCommand(Command command, boolean ajax) {
        this.command = command;
        this.ajax = ajax;
    }

    static AppCommand of(String name) {
        for (AppCommand c : AppCommand.values()) {
            if (c.name().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return DEFAULT;
    }

    public Command getCommand() {
        return command;
    }

    public boolean isAjax() {
        return ajax;
    }
}
