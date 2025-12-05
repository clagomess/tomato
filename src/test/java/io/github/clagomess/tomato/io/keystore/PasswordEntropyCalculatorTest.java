package io.github.clagomess.tomato.io.keystore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.github.clagomess.tomato.io.keystore.PasswordEntropyCalculator.Symbol.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordEntropyCalculatorTest {
    private final PasswordEntropyCalculator calculator = new PasswordEntropyCalculator();

    private static Stream<Arguments> provideSymbols(){
        return Stream.of(
                Arguments.of("\r", CONTROL),
                Arguments.of("1", DIGIT),
                Arguments.of("a", LOWERCASE),
                Arguments.of("A", UPPERCASE),
                Arguments.of("@", SPECIAL),
                Arguments.of("ç", EXTENDED),
                Arguments.of("ご", UNICODE),
                Arguments.of("😂", UNICODE)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSymbols")
    void getSymbol(
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
    void getUniqueSymbols(String password, int expected){
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
    void calculateEntropy(
            String password,
            int expectedBits,
            PasswordEntropyCalculator.EntropyStrength expectedStrength
    ){
        var result = calculator.calculateEntropy(password);

        assertEquals(expectedBits, result.getBits());
        assertEquals(expectedStrength, result.getStrength());
    }
}
