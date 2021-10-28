package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reader implements DBEntity {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
}
