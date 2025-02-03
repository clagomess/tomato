package com.github.clagomess.tomato.io.beautifier;

import lombok.Setter;

import java.io.File;
import java.io.IOException;

@Setter
public abstract class Beautifier {
    protected File inputFile;
    protected File outputFile;
    protected ProgressFI progress = value -> {};

    public abstract void parse() throws IOException;

    @FunctionalInterface
    public interface ProgressFI {
        void setValue(int value);
    }
}
