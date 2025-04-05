package io.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.CharBuffer;

@Slf4j
public class JsonBeautifier extends Beautifier {
    private final char[] buffer = new char[8192];
    private int bufferReadSize = 0;
    private int jsonCurrentSize = 0;

    private char currChar = 0;
    private int jsonPosition = -1;

    protected char nextChar() throws IOException {
        jsonPosition++;

        if(jsonCurrentSize - jsonPosition <= 0) {
            bufferReadSize = reader.read(buffer);
            jsonCurrentSize += bufferReadSize;
            progress.setValue(jsonCurrentSize);
        }

        if(jsonPosition < bufferReadSize){
            currChar = buffer[jsonPosition];
        }else{
            currChar = buffer[bufferReadSize - (jsonCurrentSize - jsonPosition)];
        }

        return currChar;
    }

    private final CharBuffer buffString = CharBuffer.allocate(8192);
    private void write(char c) throws IOException {
        if(!buffString.hasRemaining()){
            writer.write(buffString.array(), 0, buffString.position());
            buffString.clear();
        }

        buffString.put(c);
    }

    private void write(String str) throws IOException {
        if(buffString.remaining() < str.length()){
            writer.write(buffString.array(), 0, buffString.position());
            buffString.clear();
        }

        buffString.put(str);
    }

    private int identLevel = 1;
    private final char[] identBuff = " ".repeat(512).toCharArray();
    protected void writeIdent() throws IOException {
        int identLen = identLevel * 2;

        if(buffString.remaining() < identLen){
            writer.write(buffString.array(), 0, buffString.position());
            buffString.clear();
        }

        buffString.put(identBuff, 0, identLen);
    }

    @Override
    public void parse() throws IOException {
        do nextChar(); while(allowedWhitespace(currChar));

        try {
            switch (currChar) {
                case '{': {
                    write("{\n");
                    parseObject();
                    write("\n}");
                    break;
                }
                case '[': {
                    write("[\n");
                    parseArray();
                    write("\n]");
                    break;
                }
                default:
                    throw new BeautifierException(currChar, jsonPosition);
            }
        } catch (BeautifierException e) {
            log.warn(e.getMessage());
            write('\n');
            write(e.getMessage());
        }

        writer.write(buffString.array(), 0, buffString.position());
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
        while (bufferReadSize != -1){
            do nextChar(); while(currChar == '{' || allowedWhitespace(currChar));

            if(currChar == ','){
                write(",\n");
                continue;
            }

            if(currChar == '}') break;

            if (currChar == '"') {
                writeIdent();
                parseString();
                do nextChar(); while (allowedWhitespace(currChar));
            } else {
                throw new BeautifierException(currChar, jsonPosition);
            }

            if(currChar == ':'){
                write(": ");
                do nextChar(); while (allowedWhitespace(currChar));
                parseValue();
            }else{
                throw new BeautifierException(currChar, jsonPosition);
            }

            if(currChar == '}') break;
            if(currChar == ','){
                write(",\n");
            }
        }
    }

    protected void parseArray() throws IOException, BeautifierException {
        while (bufferReadSize != -1){
            if(currChar == ']') break;
            if(currChar == ',') {
                write(",\n");
            }

            do nextChar(); while (allowedWhitespace(currChar));
            if(currChar == ',') continue;
            if(currChar == ']') break;

            writeIdent();
            parseValue();
        }
    }

    protected void parseValue() throws IOException, BeautifierException {
        switch (currChar) {
            case '"': {
                parseString();
                break;
            }
            case 'n', 't': {
                write(currChar);
                for(int i = 0; i < 3; i++){
                    write(nextChar());
                }
                break;
            }
            case 'f': {
                write(currChar);
                for(int i = 0; i < 4; i++){
                    write(nextChar());
                }
                break;
            }
            case '{': {
                write("{\n");
                identLevel++;
                parseObject();
                identLevel--;
                write('\n');
                writeIdent();
                write('}');
                nextChar();
                break;
            }
            case '[': {
                write("[\n");
                identLevel++;
                parseArray();
                identLevel--;
                write('\n');
                writeIdent();
                write(']');
                break;
            }
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9': {
                parseNumber();
                break;
            }
            default: throw new BeautifierException(currChar, jsonPosition);
        }
    }

    protected void parseString() throws IOException {
        char prevChar = 0;
        write('"');

        while(bufferReadSize != -1){
            if(nextChar() == '"' && prevChar != '\\') break;
            write(currChar);

            if(prevChar == '\\' && currChar == '\\'){
                prevChar = 0;
            }else {
                prevChar = currChar;
            }
        }

        write('"');
    }

    protected void parseNumber() throws IOException {
        write(currChar);

        while(bufferReadSize != -1){
            if(!allowedNumbers(nextChar())) break;
            write(currChar);
        }
    }
}
