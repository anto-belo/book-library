package com.itechart.lab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnumEntity implements DBEntity {
    private Integer id;
    private String value;

    public EnumEntity(String value) {
        this(null, value);
    }
}
