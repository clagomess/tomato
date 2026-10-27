package io.github.clagomess.tomato.ui.component.syntaxtextarea;

import io.github.clagomess.tomato.exception.TomatoException;
import io.github.clagomess.tomato.io.http.MediaType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static io.github.clagomess.tomato.io.http.MediaType.*;

public class TomatoRSyntaxTextArea extends RSyntaxTextArea {
    private static final Theme theme;

    static {
        try {
            theme = Theme.load(TomatoRSyntaxTextArea.class.getResourceAsStream(
                    "trsyntax-textarea-theme.xml"
            ));
        } catch (IOException e) {
            throw new TomatoException(e);
        }
    }

    private final List<Consumer<String>> onChangeList = new LinkedList<>();

    public TomatoRSyntaxTextArea() {
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        setCodeFoldingEnabled(true);
        setLineWrap(false);
        setHighlightSecondaryLanguages(false);
        theme.apply(this);

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}

            public void update(){
                onChangeList.forEach(ch -> ch.accept(getText()));
            }
        });
    }

    public void setSyntaxStyle(MediaType contentType){
        if(contentType.isCompatible(APPLICATION_JSON)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            return;
        }

        if(contentType.isCompatible(TEXT_HTML)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
            return;
        }

        if(contentType.isCompatible(TEXT_XML)) {
            setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            return;
        }

        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
    }

    public void setText(String text) {
        super.setText(text);
        setCaretPosition(0);
    }

    public void reset(){
        try {
            getDocument().remove(0, getDocument().getLength());
        }catch (Exception e) {
            throw new TomatoException(e);
        }
    }

    public void addOnChange(Consumer<String> value){
        onChangeList.add(value);
    }
}
