package com.github.clagomess.tomato.io.keystore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

public class PasswordEntropyCalculator {
    @Getter
    @RequiredArgsConstructor
    public enum Symbol {
        CONTROL(32),
        DIGIT(10),
        LOWERCASE(26),
        UPPERCASE(26),
        SPECIAL(34),
        EXTENDED(128),
        UNICODE(65279);

        private final int length;
    }

    protected Symbol getSymbol(char ch){
        if(ch < 32) return Symbol.CONTROL;
        if(ch >= 48 && ch <= 57) return Symbol.DIGIT;
        if(ch >= 65 && ch <= 90) return Symbol.UPPERCASE;
        if(ch >= 97 && ch <= 122) return Symbol.LOWERCASE;
        if(ch <= 127) return Symbol.SPECIAL;
        if(ch <= 255) return Symbol.EXTENDED;

        return Symbol.UNICODE;
    }

    protected Set<Character> getUniqueSymbols(String password){
        if(password == null) return Set.of();

        Set<Character> result = new TreeSet<>();

        for(char ch : password.toCharArray()){
            result.add(ch);
        }

        return result;
    }

    public Entropy calculateEntropy(String password){
        Set<Character> uniqueSymbols = getUniqueSymbols(password);
        if(uniqueSymbols.isEmpty()) return new Entropy(0, 0, 0);

        int symbolLength = uniqueSymbols.stream()
                .map(this::getSymbol)
                .distinct()
                .map(Symbol::getLength)
                .reduce(0, Integer::sum);

        int bits = BigInteger.valueOf(symbolLength)
                .pow(uniqueSymbols.size())
                .bitLength();

        return new Entropy(password.length(), uniqueSymbols.size(), bits);
    }

    public enum EntropyStrength {
        LOW, MEDIUM, HIGH
    }

    @Getter
    public static class Entropy {
        private final int charLength;
        private final int uniqueCharLength;
        private final int bits;
        private final EntropyStrength strength;

        public Entropy(int charLength, int uniqueCharLength, int bits) {
            this.charLength = charLength;
            this.uniqueCharLength = uniqueCharLength;
            this.bits = bits;
            this.strength = calculateStrength();
        }

        private EntropyStrength calculateStrength(){
            if(bits < 45) return EntropyStrength.LOW;
            if(bits < 80) return EntropyStrength.MEDIUM;
            return EntropyStrength.HIGH;
        }
    }
}
