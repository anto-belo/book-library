package com.itechart.lab.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Command {
    static AppCommand withName(String name) {
        return AppCommand.of(name);
    }

    default CommandResponse execute(HttpServletRequest request) {
        throw new UnsupportedOperationException();
    }

    default void execute(HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }
}
