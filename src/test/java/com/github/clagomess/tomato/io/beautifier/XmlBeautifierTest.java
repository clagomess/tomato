package com.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class XmlBeautifierTest {

    @Test
    @Disabled
    public void performance() throws IOException {
        File inputFile = new File(Objects.requireNonNull(
                getClass().getResource("large.xml")
        ).getFile());

        var beautifier = new XmlBeautifier();
        beautifier.setInputFile(inputFile);
        beautifier.setOutputFile(new File("target/JsonBeautifierTest.performance.xml"));
        beautifier.parse();
    }

    private void assertXml(String input, String expected) throws IOException {
        File inputFile = new File("target/JsonBeautifierTest.assertXml.input.xml");
        Files.writeString(inputFile.toPath(), input);

        File outputFile = new File("target/JsonBeautifierTest.assertXml.output.xml");
        Files.writeString(outputFile.toPath(), input);

        var beautifier = new XmlBeautifier();
        beautifier.setInputFile(inputFile);
        beautifier.setOutputFile(outputFile);
        beautifier.parse();

        Assertions.assertThat(outputFile)
                .content()
                .containsIgnoringNewLines(expected);
    }

    private static Stream<Arguments> provide_parseBasic(){
        return Stream.of(
                Arguments.of("<root></root>", "<root/>"),
                Arguments.of("<root><child><subchild></subchild></child></root>", """
                <root>
                    <child>
                        <subchild/>
                    </child>
                </root>
                """),
                Arguments.of("<root><child1></child1><child2></child2></root>", """
                <root>
                    <child1/>
                    <child2/>
                </root>
                """),
                Arguments.of(
                        """
                        <root>
                            <foo>bar</foo>
                        </root>
                        """,
                        """
                        <root>
                            <foo>bar</foo>
                        </root>
                        """
                )

        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseBasic")
    public void parseBasic(
            String input,
            String expected
    ) throws IOException {
        assertXml(input, expected);
    }

    private static Stream<Arguments> provide_parseBasicError(){
        return Stream.of(
                Arguments.of("<root>&foo</root>", "\njavax.xml.transform.TransformerException:"),
                Arguments.of("<root><child><subchild></subchild></child></root>", """
                <root>
                    <child>
                        <subchild/>
                    </child>
                </root>
                """)

        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseBasicError")
    public void parseBasicError(
            String input,
            String expected
    ) throws IOException {
        assertXml(input, expected);
    }
}
