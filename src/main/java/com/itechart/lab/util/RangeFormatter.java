package com.itechart.lab.util;

import java.util.List;

public class RangeFormatter {
    private RangeFormatter() {
    }

    public static String format(List<Integer> values) {
        if (values.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(values.get(0) + "");
        for (int i = 1; i < values.size(); i++) {
            sb.append(", ").append(values.get(i));
        }
        return sb.toString();
    }

    public static String format(List<String> values, boolean quotes) {
        if (values.isEmpty()) {
            return "";
        }
        String value = quotes ? "'" + values.get(0) + "'" : values.get(0);
        StringBuilder sb = new StringBuilder(value);
        for (int i = 1; i < values.size(); i++) {
            value = quotes ? "'" + values.get(i) + "'" : values.get(i);
            sb.append(", ").append(value);
        }
        return sb.toString();
    }
}
