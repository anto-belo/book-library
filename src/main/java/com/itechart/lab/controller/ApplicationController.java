package com.itechart.lab.controller;

import com.itechart.lab.command.AppCommand;
import com.itechart.lab.command.Command;
import com.itechart.lab.command.CommandResponse;
import com.itechart.lab.connection.ConnectionManager;
import lombok.extern.log4j.Log4j2;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@WebServlet(urlPatterns = "/controller")
public class ApplicationController extends HttpServlet {
    private static final String COMMAND_PARAMETER_NAME = "command";

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException, ServletException {
        if (!ConnectionManager.getManager().isRunning()) {
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/fatal.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        String commandName = req.getParameter(COMMAND_PARAMETER_NAME);
        AppCommand command = Command.withName(commandName);
        if (command.isAjax()) {
            command.getCommand().execute(req, resp);
            return;
        }

        CommandResponse response = command.getCommand().execute(req);
        if (response.redirect()) {
            resp.sendRedirect(response.location());
        } else {
            RequestDispatcher dispatcher = req.getRequestDispatcher(response.location());
            dispatcher.forward(req, resp);
        }
    }
}
