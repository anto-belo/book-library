package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrow implements DBEntity {
    private int id;
    private int readerId;
    private int bookId;
    private Timestamp borrowDate;
    private int forMonths;
    private Timestamp returnDate;
    private String comment;
    private Integer borrowStatus;
}
