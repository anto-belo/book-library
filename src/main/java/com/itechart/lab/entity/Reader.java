package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reader implements DBEntity {
    private Integer id;
    private String name;
    private String email;

    public Reader(String name, String email) {
        this(null, name, email);
    }
}
