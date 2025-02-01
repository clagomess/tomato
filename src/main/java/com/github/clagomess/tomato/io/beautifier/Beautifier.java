package com.github.clagomess.tomato.io.beautifier;

import lombok.Setter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Setter
public abstract class Beautifier {
    protected BufferedReader reader;
    protected BufferedWriter writer;

    public abstract void parse() throws IOException;
}
