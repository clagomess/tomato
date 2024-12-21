package com.github.clagomess.tomato.service.http;

import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class HttpLogCollectorUtil extends Logger {
    private final StringBuilder logText = new StringBuilder();

    public HttpLogCollectorUtil() {
        super("Jersey", null);
    }

    @Override
    public void log(Level level, String msg) {
        logText.append(msg).append("\n");
    }

    public void flush() {
        logText.delete(0, logText.length());
    }
}
