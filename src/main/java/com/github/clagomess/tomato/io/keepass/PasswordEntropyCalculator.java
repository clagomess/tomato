package com.github.clagomess.tomato.io.keepass;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Set;
import java.util.TreeSet;

public class PasswordEntropyCalculator {
    protected int getSymbolLength(char ch){
        if(ch < 32) return 32;
        if(ch >= 48 && ch <= 57) return 10;
        if(ch >= 65 && ch <= 90) return 26;
        if(ch >= 97 && ch <= 122) return 26;
        if(ch <= 127) return 34;
        if(ch <= 255) return 128;

        return 65279;
    }

    protected Set<Character> getUniqueSymbols(String password){
        if(password == null) return Set.of();

        Set<Character> result = new TreeSet<>();

        for(char ch : password.toCharArray()){
            result.add(ch);
        }

        return result;
    }

    protected Entropy calculateEntropy(String password){
        Set<Character> uniqueSymbols = getUniqueSymbols(password);
        if(uniqueSymbols.isEmpty()) return new Entropy(0, 0, 0);

        int symbolLength = uniqueSymbols.stream()
                .map(this::getSymbolLength)
                .distinct()
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
