package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrow implements DBEntity {
    private Integer id;
    private int readerId;
    private int bookId;
    private Date borrowDate;
    private int forMonths;
    private Date returnDate;
    private String comment;
    private Integer borrowStatus;

    public Borrow(int readerId, int bookId, Date borrowDate, int forMonths,
                  Date returnDate, String comment, Integer borrowStatus) {
        this(null, readerId, bookId, borrowDate, forMonths, returnDate, comment, borrowStatus);
    }
}
