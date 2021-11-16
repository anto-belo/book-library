package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book implements DBEntity {
    private Integer id;
    private String coverUrl;
    private String title;
    private int publisherId;
    private int publishYear;
    private int pageCount;
    private String isbn;
    private int totalAmount;
    private int remainingAmount;
    private String description;
}
