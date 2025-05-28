package io.github.clagomess.tomato.exception;

public class ConverterTypeEmptyException extends TomatoException{
    public ConverterTypeEmptyException() {
        super("Converter type not selected");
    }
}
