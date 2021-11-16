package com.itechart.lab.command;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public interface CommandResponse {
    String MSG_CANT_SEND_JSON = "Can't send JSON string";
    String MSG_CANT_SERIALIZE_TO_JSON = "Can't serialize object to JSON string";

    CommandResponse MAIN = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/main.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    CommandResponse BOOK = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/book.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    CommandResponse SEARCH = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/search.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    CommandResponse ERROR_500 = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/error500.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    CommandResponse ERROR_404 = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/error404.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    CommandResponse ERROR_400 = new CommandResponse() {
        @Override
        public String location() {
            return "/WEB-INF/jsp/error400.jsp";
        }

        @Override
        public boolean redirect() {
            return false;
        }
    };

    static boolean sendJson(HttpServletResponse response, String json) {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(json);
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    String location();

    boolean redirect();
}
