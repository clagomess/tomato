package com.github.clagomess.tomato.util;

import lombok.Getter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerHandlerUtil extends Handler {
    @Getter
    private final StringBuilder logText = new StringBuilder();

    @Override
    public void publish(LogRecord record) {
        logText.append(record.getMessage());
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
