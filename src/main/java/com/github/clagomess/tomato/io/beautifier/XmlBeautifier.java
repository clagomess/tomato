package com.github.clagomess.tomato.io.beautifier;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class XmlBeautifier extends Beautifier {
    @Override
    public void parse() throws IOException {
        try(
                var fis = new FileInputStream(inputFile);
                var fos = new FileOutputStream(outputFile);
        ) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            transformer.transform(
                    new StreamSource(fis),
                    new StreamResult(fos)
            );
        } catch (TransformerException e) {
            log.warn(e.getMessage());
        }
    }
}
