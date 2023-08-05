package ru.practicum.ewm.util;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final String APP_NAME = "ewm-main-service";

    private Constants() {
    }
}