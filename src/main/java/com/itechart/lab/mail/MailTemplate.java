package com.itechart.lab.mail;

import org.stringtemplate.v4.ST;

public class MailTemplate {
    private static final String MAIL_TEXT_BEFORE_TEMPLATE = "Dear <name>! We remind you that " +
            "the borrow of the book <book-title> expires in <expire-date>. If you have any " +
            "questions, you can contact the library by phone <lib-phone> or email <lib-mail>";
    private static final String MAIL_TEXT_AFTER_TEMPLATE = "Dear <name>! We remind you that the " +
            "borrow of the book <book-title> expired yesterday. Please return the book as soon " +
            "as possible. If you have any questions, you can contact the library by phone " +
            "<lib-phone> or email <lib-email>";

    private static final String NAME_ATTRIB = "name";
    private static final String BOOK_TITLE_ATTRIB = "book-title";
    private static final String EXPIRE_DATE_ATTRIB = "expire-date";
    private static final String LIBRARY_PHONE_ATTRIB = "lib-phone";
    private static final String LIBRARY_EMAIL_ATTRIB = "lib-email";

    private static final String LIBRARY_PHONE = "+1 (23) 456-7-890";
    private static final String LIBRARY_EMAIL = "library@gmail.com";

    public static String template(boolean notifyBefore, String name,
                                  String bookTitle, String expireDate) {
        ST msg = notifyBefore ? new ST(MAIL_TEXT_BEFORE_TEMPLATE)
                : new ST(MAIL_TEXT_AFTER_TEMPLATE);
        msg.add(NAME_ATTRIB, name);
        msg.add(BOOK_TITLE_ATTRIB, bookTitle);
        msg.add(EXPIRE_DATE_ATTRIB, expireDate);
        msg.add(LIBRARY_PHONE_ATTRIB, LIBRARY_PHONE);
        msg.add(LIBRARY_EMAIL_ATTRIB, LIBRARY_EMAIL);
        return msg.render();
    }
}
