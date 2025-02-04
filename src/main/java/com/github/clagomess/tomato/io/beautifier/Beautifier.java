package com.github.clagomess.tomato.io.beautifier;

import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Setter
public abstract class Beautifier {
    protected File inputFile;
    protected File outputFile;
    protected Charset charset = StandardCharsets.UTF_8;
    protected ProgressFI progress = value -> {};

    public abstract void parse() throws IOException;

    @FunctionalInterface
    public interface ProgressFI {
        void setValue(int value);
    }
}
