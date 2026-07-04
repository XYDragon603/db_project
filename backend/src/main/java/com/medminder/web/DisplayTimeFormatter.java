package com.medminder.web;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DisplayTimeFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DisplayTimeFormatter() {}

    public static String format(LocalTime time) {
        return time.format(FORMATTER);
    }
}
