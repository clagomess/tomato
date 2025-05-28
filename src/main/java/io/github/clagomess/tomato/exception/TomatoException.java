package io.github.clagomess.tomato.exception;

public class TomatoException extends RuntimeException {
    public TomatoException(String message) {
        super(message);
    }

    public TomatoException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
