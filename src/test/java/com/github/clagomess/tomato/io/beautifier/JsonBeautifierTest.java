package com.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class JsonBeautifierTest {
    @Test
    @Disabled
    public void performance() throws IOException {
        try(
                var reader = new BufferedReader(new FileReader(
                        Objects.requireNonNull(getClass().getResource("large.json")).getFile()
                ));

                var writer = new BufferedWriter(new FileWriter("target/JsonBeautifierTest.performance.json"));
        ) {
            var beautifier = new JsonBeautifier();
            beautifier.setReader(reader);
            beautifier.setWriter(writer);
            beautifier.parse();
        }
    }

    private void assertJson(String input, String expected) throws IOException {
        var result = new StringWriter();

        try(
                var reader = new BufferedReader(new StringReader(input));
                var writer = new BufferedWriter(result)
        ){
            var beautifier = new JsonBeautifier();
            beautifier.setReader(reader);
            beautifier.setWriter(writer);
            beautifier.parse();
        }

        Assertions.assertThat(result.toString())
                .isEqualToIgnoringNewLines(expected);
    }

    @Test
    public void parse_full() throws IOException {
        var input = """
        {
          "name": "foo",
          "age": 12,
          "median": 12.45e10,
          "balance": -35,
          "sum": 14.5,
          "average": 34.6E5,
          "content": ["apple", "juice", 12.9, {"bad":  "vibe"}],
          "hasPeople": true,
          "hasZombie": false,
          "department": null,
          "cars": {
            "model": "T"
          }
        }
        """;

        var expected = """
        {
          "name": "foo",
          "age": 12,
          "median": 12.45e10,
          "balance": -35,
          "sum": 14.5,
          "average": 34.6E5,
          "content": [
            "apple",
            "juice",
            12.9,
            {
              "bad": "vibe"
            }
          ],
          "hasPeople": true,
          "hasZombie": false,
          "department": null,
          "cars": {
            "model": "T"
          }
        }
        """;

        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parse_ROOT_array() {
        return Stream.of(
                Arguments.of("[69]", """
                [
                  69
                ]
                """),
                Arguments.of("[69,69]", """
                [
                  69,
                  69
                ]
                """),
                Arguments.of("\n [ 69 , 69 ] ", """
                [
                  69,
                  69
                ]
                """),
                Arguments.of("""
                [{"key": "foo","value": "Temp\\\\"},
                {"key": "foo","value": "bar"}]
                """, """
                [
                  {
                    "key": "foo",
                    "value": "Temp\\\\"
                  },
                  {
                    "key": "foo",
                    "value": "bar"
                  }
                ]
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parse_ROOT_array")
    public void parse_ROOT_array(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }


    private static Stream<Arguments> provide_parse_error() {
        return Stream.of(
                Arguments.of("  <body>{\"x\":69}", """
                
                Unexpected character '<' in original position at 2
                """),
                Arguments.of(" array[]", """
                
                Unexpected character 'a' in original position at 1
                """),
                Arguments.of("\n-{\"x\":69}", """
                
                Unexpected character '-' in original position at 1
                """),
                Arguments.of("8\"x\":69}", """
                
                Unexpected character '8' in original position at 0
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parse_error")
    public void parse_error(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parse_ROOT_object() {
        return Stream.of(
                Arguments.of("{\"x\":69}", """
                {
                  "x": 69
                }
                """),
                Arguments.of("{\"x\":69,\"y\":69}", """
                {
                  "x": 69,
                  "y": 69
                }
                """),
                Arguments.of("\n {\"x\":69, \n \"y\" : 69 }\n\n", """
                {
                  "x": 69,
                  "y": 69
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parse_ROOT_object")
    public void parse_ROOT_object(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":{}}}",
            "{\"a\":\"va\",\"vl\":{ }}",
            "{\"a\" : \"va\", \"vl\" : {} }",
            "{\"a\" : \"va\", \"vl\" : { } }",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n{}\n}",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n{ }\n}",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n{\n}\n}",
    })
    public void parseObject_Empty(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": {
        
          }
        }
        """;
        assertJson(input, expected);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":{\"x\":69}}",
            "{\"a\" : \"va\", \"vl\" : {\"x\":69} }",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n{\"x\":69}\n}",
    })
    public void parseObject_SingleValue(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": {
            "x": 69
          }
        }
        """;
        assertJson(input, expected);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":{\"x\":69,\"y\":00},\"c\":\"vc\"}",
            " {\"a\" : \"va\" , \"vl\" : {\"x\":69,\"y\":00} , \"c\" : \"vc\" } ",
            "\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\n{\"x\":69,\"y\":00}\n,\n\"c\"\n:\n\"vc\"\n}\n",
    })
    public void parseObject_MutipleValue(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": {
            "x": 69,
            "y": 00
          },
          "c": "vc"
        }
        """;
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseObject_Error() {
        return Stream.of(
                Arguments.of("{69}", """
                {
                
                Unexpected character '6' in original position at 1
                """),
                Arguments.of("{\"x\"-69}", """
                {
                  "x"
                Unexpected character '-' in original position at 4
                """),
                Arguments.of("{\"x\" - 69}", """
                {
                  "x"
                Unexpected character '-' in original position at 5
                """),
                Arguments.of("{\"x\" -69}", """
                {
                  "x"
                Unexpected character '-' in original position at 5
                """),
                Arguments.of("{\"x\" :}", """
                {
                  "x":\s
                Unexpected character '}' in original position at 6
                """),
                Arguments.of("{\"x\" :69, \"y\"}", """
                {
                  "x": 69,
                  "y"
                Unexpected character '}' in original position at 13
                """),
                Arguments.of("{\"x\" :69, \"y\" -}", """
                {
                  "x": 69,
                  "y"
                Unexpected character '-' in original position at 14
                """),
                Arguments.of("{\"x\" :69, \"y\" - 9}", """
                {
                  "x": 69,
                  "y"
                Unexpected character '-' in original position at 14
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseObject_Error")
    public void parseObject_Error(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":[]}",
            "{\"a\":\"va\",\"vl\":[ ]}",
            "{\"a\" : \"va\", \"vl\" : [] }",
            "{\"a\" : \"va\", \"vl\" : [ ] }",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n[]\n}",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n[ ]\n}",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n[\n]\n}",
    })
    public void parseArray_Empty(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": [
        
          ]
        }
        """;
        assertJson(input, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":[69]}",
            "{\"a\" : \"va\", \"vl\" : [69] }",
            "{\"a\"\n:\n\"va\",\n\"vl\"\n:\n[69]\n}",
    })
    public void parseArray_SingleValue(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": [
            69
          ]
        }
        """;
        assertJson(input, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"a\":\"va\",\"vl\":[69,00],\"c\":\"vc\"}",
            " {\"a\" : \"va\" , \"vl\" : [69,00] , \"c\" : \"vc\" } ",
            "\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\n[69,00]\n,\n\"c\"\n:\n\"vc\"\n}\n",
    })
    public void parseArray_MutipleValue(
            String input
    ) throws IOException {
        String expected = """
        {
          "a": "va",
          "vl": [
            69,
            00
          ],
          "c": "vc"
        }
        """;
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseValue_Error() {
        return Stream.of(
                Arguments.of("{\"x\"::69}", """
                {
                  "x":\s
                Unexpected character ':' in original position at 5
                """),
                Arguments.of("{\"x\":True}", """
                {
                  "x":\s
                Unexpected character 'T' in original position at 5
                """),
                Arguments.of("{\"x\":NULL}", """
                {
                  "x":\s
                Unexpected character 'N' in original position at 5
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseValue_Error")
    public void parseValue_Error(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseString_Values() {
        return Stream.of(
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\bass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\bass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\fass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\fass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\nass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\nass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\r\\nass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\r\\nass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\tass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\tass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\u0DDAass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\u0DDAass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo/ass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo/ass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\\\ass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\\\ass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\\"ass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\"ass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo\\\"ass\\\"\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo\\"ass\\"",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \"foo ass\", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": "foo ass",
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": \" foo ass \", \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": " foo ass ",
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseString_Values")
    public void parseString_Values(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseNumber_Values() {
        return Stream.of(
                Arguments.of("{\"a\": \"va\", \"vl\": 1, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": 1,
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": 12, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": 12,
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": 12.45e10, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": 12.45e10,
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": -35, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": -35,
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": 14.5, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": 14.5,
                  "c": "vc"
                }
                """),
                Arguments.of("{\"a\": \"va\", \"vl\": 34.6E5, \"c\": \"vc\"}", """
                {
                  "a": "va",
                  "vl": 34.6E5,
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseNumber_Values")
    public void parseNumber_Values(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseNumber_WS() {
        return Stream.of(
                Arguments.of("{\"a\":\"va\",\"vl\":69}", """
                {
                  "a": "va",
                  "vl": 69
                }
                """),
                Arguments.of("{\"a\" : \"va\", \"vl\" : 69 }", """
                {
                  "a": "va",
                  "vl": 69
                }
                """),
                Arguments.of("{\"a\"\n:\n\"va\",\n\"vl\"\n:\n69\n}", """
                {
                  "a": "va",
                  "vl": 69
                }
                """),
                Arguments.of("{\"a\":\"va\",\"vl\":69,\"c\":\"vc\"}", """
                {
                  "a": "va",
                  "vl": 69,
                  "c": "vc"
                }
                """),
                Arguments.of(" {\"a\" : \"va\" , \"vl\" : 69 , \"c\" : \"vc\" } ", """
                {
                  "a": "va",
                  "vl": 69,
                  "c": "vc"
                }
                """),
                Arguments.of("\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\n69\n,\n\"c\"\n:\n\"vc\"\n}\n", """
                {
                  "a": "va",
                  "vl": 69,
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseNumber_WS")
    public void parseNumber_WS(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseBoolean_True() {
        return Stream.of(
                Arguments.of("{\"a\":\"va\",\"vl\":true}", """
                {
                  "a": "va",
                  "vl": true
                }
                """),
                Arguments.of("{\"a\" : \"va\", \"vl\" : true }", """
                {
                  "a": "va",
                  "vl": true
                }
                """),
                Arguments.of("{\"a\"\n:\n\"va\",\n\"vl\"\n:\ntrue\n}", """
                {
                  "a": "va",
                  "vl": true
                }
                """),
                Arguments.of("{\"a\":\"va\",\"vl\":true,\"c\":\"vc\"}", """
                {
                  "a": "va",
                  "vl": true,
                  "c": "vc"
                }
                """),
                Arguments.of(" {\"a\" : \"va\" , \"vl\" : true , \"c\" : \"vc\" } ", """
                {
                  "a": "va",
                  "vl": true,
                  "c": "vc"
                }
                """),
                Arguments.of("\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\ntrue\n,\n\"c\"\n:\n\"vc\"\n}\n", """
                {
                  "a": "va",
                  "vl": true,
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseBoolean_True")
    public void parseBoolean_True(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseBoolean_False() {
        return Stream.of(
                Arguments.of("{\"a\":\"va\",\"vl\":false}", """
                {
                  "a": "va",
                  "vl": false
                }
                """),
                Arguments.of("{\"a\" : \"va\", \"vl\" : false }", """
                {
                  "a": "va",
                  "vl": false
                }
                """),
                Arguments.of("{\"a\"\n:\n\"va\",\n\"vl\"\n:\nfalse\n}", """
                {
                  "a": "va",
                  "vl": false
                }
                """),
                Arguments.of("{\"a\":\"va\",\"vl\":false,\"c\":\"vc\"}", """
                {
                  "a": "va",
                  "vl": false,
                  "c": "vc"
                }
                """),
                Arguments.of(" {\"a\" : \"va\" , \"vl\" : false , \"c\" : \"vc\" } ", """
                {
                  "a": "va",
                  "vl": false,
                  "c": "vc"
                }
                """),
                Arguments.of("\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\nfalse\n,\n\"c\"\n:\n\"vc\"\n}\n", """
                {
                  "a": "va",
                  "vl": false,
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseBoolean_False")
    public void parseBoolean_False(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseBoolean_Error() {
        return Stream.of(
                Arguments.of("{\"x\":fALSE}", """
                {
                  "x": fALSE
                }
                """),
                Arguments.of("{\"x\":fSEAL}", """
                {
                  "x": fSEAL
                }
                """),
                Arguments.of("{\"x\":tRue}", """
                {
                  "x": tRue
                }
                """),
                Arguments.of("{\"x\":teuR}", """
                {
                  "x": teuR
                }
                """),
                Arguments.of("{\"x\":truE}", """
                {
                  "x": truE
                }
                """),
                Arguments.of("{\"x\":falsE}", """
                {
                  "x": falsE
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseBoolean_Error")
    public void parseBoolean_Error(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseNull() {
        return Stream.of(
                Arguments.of("{\"a\":\"va\",\"vl\":null}", """
                {
                  "a": "va",
                  "vl": null
                }
                """),
                Arguments.of("{\"a\" : \"va\", \"vl\" : null }", """
                {
                  "a": "va",
                  "vl": null
                }
                """),
                Arguments.of("{\"a\"\n:\n\"va\",\n\"vl\"\n:\nnull\n}", """
                {
                  "a": "va",
                  "vl": null
                }
                """),
                Arguments.of("{\"a\":\"va\",\"vl\":null,\"c\":\"vc\"}", """
                {
                  "a": "va",
                  "vl": null,
                  "c": "vc"
                }
                """),
                Arguments.of(" {\"a\" : \"va\" , \"vl\" : null , \"c\" : \"vc\" } ", """
                {
                  "a": "va",
                  "vl": null,
                  "c": "vc"
                }
                """),
                Arguments.of("\n{\"a\"\n:\n\"va\"\n,\n\"vl\"\n:\nnull\n,\n\"c\"\n:\n\"vc\"\n}\n", """
                {
                  "a": "va",
                  "vl": null,
                  "c": "vc"
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseNull")
    public void parseNull(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }

    private static Stream<Arguments> provide_parseNull_Error() {
        return Stream.of(
                Arguments.of("{\"x\":nUll}", """
                {
                  "x": nUll
                }
                """),
                Arguments.of("{\"x\":nULL}", """
                {
                  "x": nULL
                }
                """),
                Arguments.of("{\"x\":nulL}", """
                {
                  "x": nulL
                }
                """)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_parseNull_Error")
    public void parseNull_Error(
            String input,
            String expected
    ) throws IOException {
        assertJson(input, expected);
    }
}
