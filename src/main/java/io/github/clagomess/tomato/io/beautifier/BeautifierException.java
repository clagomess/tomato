package io.github.clagomess.tomato.io.beautifier;

public class BeautifierException extends Exception {
    public BeautifierException(char c, int pos) {
        super(String.format("Unexpected character '%c' in original position at %d", c, pos));
    }
}
