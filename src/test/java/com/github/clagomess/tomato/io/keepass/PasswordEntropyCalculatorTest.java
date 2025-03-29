package com.github.clagomess.tomato.io.keepass;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEntropyCalculatorTest {
    private final PasswordEntropyCalculator calculator = new PasswordEntropyCalculator();

    @ParameterizedTest
    @CsvSource(value = {
            "\r,32",
            "1,10",
            "a,26",
            "A,26",
            "@,34",
            "ç,128",
            "ご,65279",
            "😂,65279",
    }, ignoreLeadingAndTrailingWhitespace = false)
    public void getSymbolLength(String ch, int expected) {
        assertEquals(
                expected,
                calculator.getSymbolLength(ch.charAt(0))
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",0",
            "a,1",
            "aaa,1",
            "aaa1,2",
            "ごご,1",
            "😂,2",
    })
    public void getUniqueSymbols(String password, int expected){
        assertEquals(
                expected,
                calculator.getUniqueSymbols(password).size()
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",0,LOW",
            "123456,20,LOW",
            "password,33,LOW",
            "qwerty,29,LOW",
            "aaaaaaaa,5,LOW",
            "00000000,4,LOW",
            "iF10!6,37,LOW",
            "$xZ6@Mg115f,62,MEDIUM",
            "😂y%a#laHgç~,176,HIGH",
            "resuITrecetreprOfolYcefAnCeIStBowkNisTaw,113,HIGH",
    })
    public void calculateEntropy(
            String password,
            int expectedBits,
            PasswordEntropyCalculator.EntropyStrength expectedStrength
    ){
        var result = calculator.calculateEntropy(password);

        assertEquals(expectedBits, result.getBits());
        assertEquals(expectedStrength, result.getStrength());
    }
}
