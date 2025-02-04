package com.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

@Slf4j
public class XmlBeautifier extends Beautifier {
    @Override
    public void parse() throws IOException {
        try(
                var reader = new BufferedReader(new FileReader(inputFile, charset));
                var writer = new BufferedWriter(new FileWriter(outputFile, charset));
        ) {
            try {
                Source style = new StreamSource(getClass()
                        .getResourceAsStream("transform-style.xsl"));

                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer(style);

                transformer.transform(
                        new StreamSource(reader),
                        new StreamResult(writer)
                );
            } catch (TransformerException e) {
                log.warn(e.getMessage());
                writer.write('\n');
                writer.write(e.getMessage());
            }
        }
    }
}
