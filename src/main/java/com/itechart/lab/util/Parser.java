package com.itechart.lab.util;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class Parser {
    private Parser() {
    }

    public static Optional<Integer> tryParseInt(String number) {
        int parsedNumber;
        try {
            parsedNumber = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
        return Optional.of(parsedNumber);
    }

    public static List<Integer> parseIntList(List<String> numbers) {
        List<Integer> parsedNumbers = new ArrayList<>();
        Optional<Integer> parsedNumber;
        for (String n : numbers) {
            parsedNumber = tryParseInt(n);
            parsedNumber.ifPresent(parsedNumbers::add);
        }
        return parsedNumbers;
    }
}
