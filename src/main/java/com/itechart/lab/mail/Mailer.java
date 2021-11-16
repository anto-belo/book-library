package com.itechart.lab.mail;

public interface Mailer {
    static Mailer getInstance() {
        return MailerImpl.getInstance();
    }

    void init();

    void destroy();
}
