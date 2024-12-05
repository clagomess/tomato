package com.github.clagomess.tomato.util;

import lombok.Getter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

@Getter
public class LoggerHandlerUtil extends Handler {
    private final StringBuilder logText = new StringBuilder();

    @Override
    public void publish(LogRecord record) {
        logText.append(record.getMessage());
    }

    @Override
    public void flush() {
        logText.delete(0, logText.length());
    }

    @Override
    public void close() throws SecurityException {}
}
