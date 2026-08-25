package io.github.clagomess.tomato.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String epochMilliToISO(long lastModified){
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastModified),
                ZoneId.systemDefault()
        ).format(ISO_FORMATTER);
    }
}
