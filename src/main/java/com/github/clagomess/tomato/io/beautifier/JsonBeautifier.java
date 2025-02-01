package com.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Slf4j
public class JsonBeautifier {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public JsonBeautifier(
            BufferedReader reader,
            BufferedWriter writer
    ) {
        this.reader = reader;
        this.writer = writer;
    }

    private final char[] buffer = new char[8192];
    private int bufferReadSize = 0;
    private int pos = 0;
    private int jsonCurrentSize = 0;

    protected char currentChar() throws IOException {
        if(jsonCurrentSize - pos <= 0) {
            bufferReadSize = reader.read(buffer);
            jsonCurrentSize += bufferReadSize;
        }

        if(pos < bufferReadSize){
            return buffer[pos];
        }else{
            return buffer[bufferReadSize - (jsonCurrentSize - pos)];
        }
    }

    private int identLevel = 1;
    private final char[] identBuff = " ".repeat(512).toCharArray();
    protected void writeIdent() throws IOException {
        writer.write(identBuff, 0, identLevel * 2);
    }

    public void parse() throws IOException {
        char c;
        while (allowedWhitespace(c = currentChar())) pos++;

        try {
            switch (c) {
                case '{': {
                    writer.write("{\n");
                    parseObject();
                    writer.write("\n}");
                    break;
                }
                case '[': {
                    writer.write("[\n");
                    parseArray();
                    writer.write("\n]");
                    break;
                }
                default:
                    throw new BeautifierException(c, pos);
            }
        }catch (BeautifierException e) {
            log.warn(e.getMessage());
            writer.newLine();
            writer.write(e.getMessage());
        }

        writer.flush();
    }

    protected boolean allowedWhitespace(char c) {
        return switch (c) {
            case (char) 0x20, (char) 0x0A, (char) 0x0D, (char) 0x09 -> true;
            default -> false;
        };
    }

    protected boolean allowedNumbers(char c) {
        return switch (c) {
            case '-', '+', '0', '1', '2', '3', '4', '5',
                 '6', '7', '8', '9', '.', 'e', 'E' -> true;
            default -> false;
        };
    }

    protected void parseObject() throws IOException, BeautifierException {
        char c;

        while (bufferReadSize != -1){
            do pos++; while (
                    (c = currentChar()) == '{' ||
                    allowedWhitespace(c)
            );

            if(c == ','){
                writer.write(",\n");
                continue;
            }

            if(c == '}') break;

            if (c == '"') {
                writeIdent();
                parseString();
                do pos++; while (allowedWhitespace(c = currentChar()));
            } else {
                throw new BeautifierException(c, pos);
            }

            if(c == ':'){
                writer.write(": ");
                do pos++; while (allowedWhitespace(currentChar()));
                parseValue();
            }else{
                throw new BeautifierException(c, pos);
            }

            c = currentChar();

            if(c == '}') break;
            if(c == ','){
                writer.write(",\n");
            }
        }
    }

    protected void parseArray() throws IOException, BeautifierException {
        char c;
        while (bufferReadSize != -1){
            c = currentChar();
            if(c == ']') break;
            if(c == ',') {
                writer.write(",\n");
            }

            do pos++; while (allowedWhitespace(c = currentChar()));
            if(c == ',') continue;
            if(c == ']') break;

            writeIdent();
            parseValue();
        }
    }

    protected void parseValue() throws IOException, BeautifierException {
        char c = currentChar();
        switch (c) {
            case '"': {
                parseString();
                break;
            }
            case 'n', 't': {
                writer.write(c);
                for(int i = 0; i < 3; i++){
                    pos++;
                    writer.write(currentChar());
                }
                break;
            }
            case 'f': {
                writer.write(c);
                for(int i = 0; i < 4; i++){
                    pos++;
                    writer.write(currentChar());
                }
                break;
            }
            case '{': {
                writer.write("{\n");
                identLevel++;
                parseObject();
                identLevel--;
                writer.newLine();
                writeIdent();
                writer.write('}');
                pos++;
                break;
            }
            case '[': {
                writer.write("[\n");
                identLevel++;
                parseArray();
                identLevel--;
                writer.newLine();
                writeIdent();
                writer.write(']');
                break;
            }
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9': {
                parseNumber();
                break;
            }
            default: throw new BeautifierException(c, pos);
        }
    }

    private final char[] buffString = new char[512];
    protected void parseString() throws IOException {
        int buffStringPos = 0;
        buffString[0] = '"';

        char prevChar = 0;

        while(bufferReadSize != -1){
            pos++;
            buffStringPos++;

            if(buffStringPos == buffString.length){
                writer.write(buffString);
                buffStringPos = 0;
            }

            char c = currentChar();
            if(c == '"' && prevChar != '\\') break;
            buffString[buffStringPos] = c;
            prevChar = c;
        }

        buffString[buffStringPos] = '"';
        writer.write(buffString, 0, buffStringPos + 1);
    }

    protected void parseNumber() throws IOException {
        char c = currentChar();
        writer.write(c);

        while(bufferReadSize != -1){
            pos++;

            c = currentChar();
            if(!allowedNumbers(c)) break;
            writer.write(c);
        }
    }
}
