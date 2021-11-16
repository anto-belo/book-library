package com.itechart.lab.command.ajax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.lab.command.Command;
import com.itechart.lab.entity.Reader;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.ReaderService;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.itechart.lab.command.CommandResponse.MSG_CANT_SEND_JSON;
import static com.itechart.lab.command.CommandResponse.MSG_CANT_SERIALIZE_TO_JSON;
import static com.itechart.lab.command.CommandResponse.sendJson;

@Log4j2
public class FindReadersByEmailLikeCommand implements Command {
    private static final FindReadersByEmailLikeCommand instance
            = new FindReadersByEmailLikeCommand();

    private static final String EMAIL_PARAMETER_NAME = "email";

    private static final String EMPTY_JSON = "{}";

    private static final String MSG_TEMPLATE_CANT_FIND_READERS
            = "Can't find readers by email pattern (%s)";
    private static final String MSG_CANT_SEND_JSON_RESPONSE
            = "Can't send JSON response";

    private FindReadersByEmailLikeCommand() {
    }

    public static FindReadersByEmailLikeCommand getInstance() {
        return instance;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter(EMAIL_PARAMETER_NAME);
        if (email == null) {
            if (!sendJson(response, EMPTY_JSON)) {
                log.error(MSG_CANT_SEND_JSON);
            }
        }
        List<Reader> readers = null;
        try {
            readers = ReaderService.getInstance().findReaderByEmailLike(email);
        } catch (ServiceException e) {
            log.error(String.format(MSG_TEMPLATE_CANT_FIND_READERS, email));
            if (!sendJson(response, EMPTY_JSON)) {
                log.error(MSG_CANT_SEND_JSON_RESPONSE);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            sendJson(response, mapper.writeValueAsString(readers));
        } catch (JsonProcessingException e) {
            log.error(MSG_CANT_SERIALIZE_TO_JSON);
            if (!sendJson(response, EMPTY_JSON)) {
                log.error(MSG_CANT_SEND_JSON);
            }
        }
    }
}
