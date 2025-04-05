package io.github.clagomess.tomato.io.repository;

import java.io.File;

public class DirectoryCreateException extends RuntimeException {
    public DirectoryCreateException(File directory) {
        super(String.format(
                "Error on create : %s",
                directory
        ));
    }
}
