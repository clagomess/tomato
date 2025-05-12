package io.github.clagomess.tomato.io.keystore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEntropyCalculatorTest {
    private final PasswordEntropyCalculator calculator = new PasswordEntropyCalculator();

    @ParameterizedTest
    @CsvSource(value = {
            "\r,CONTROL",
            "1,DIGIT",
            "a,LOWERCASE",
            "A,UPPERCASE",
            "@,SPECIAL",
            "ç,EXTENDED",
            "ご,UNICODE",
            "😂,UNICODE",
    }, ignoreLeadingAndTrailingWhitespace = false)
    public void getSymbol(
            String ch,
            PasswordEntropyCalculator.Symbol expected
    ) {
        assertEquals(
                expected,
                calculator.getSymbol(ch.charAt(0))
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
            "iF10!6,40,LOW",
            "$xZ6@Mg115f,66,MEDIUM",
            "😂y%a#laHgç~,176,HIGH",
            "resuITrecetreprOfolYcefAnCeIStBowkNisTaw,137,HIGH",
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
