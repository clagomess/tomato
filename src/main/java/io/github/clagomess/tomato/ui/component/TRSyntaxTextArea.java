package io.github.clagomess.tomato.ui.component;

import io.github.clagomess.tomato.io.http.MediaType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static io.github.clagomess.tomato.io.http.MediaType.*;

public class TRSyntaxTextArea extends RSyntaxTextArea {
    private static final Theme theme;

    static {
        try {
            theme = Theme.load(TRSyntaxTextArea.class.getResourceAsStream(
                    "trsyntax-textarea-theme.xml"
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<OnChangeFI> onChangeList = new LinkedList<>();

    public TRSyntaxTextArea() {
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        setCodeFoldingEnabled(true);
        setLineWrap(true);
        setWrapStyleWord(true);
        setAntiAliasingEnabled(true);
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
                onChangeList.forEach(ch -> ch.change(getText()));
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

    public void reset(){
        try {
            getDocument().remove(0, getDocument().getLength());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addOnChange(OnChangeFI value){
        onChangeList.add(value);
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(String content);
    }

    public static RTextScrollPane createScroll(RSyntaxTextArea textArea){
        var color = ColorConstant.GRAY;

        var sp = new RTextScrollPane(textArea);
        sp.setBorder(new MatteBorder(0, 1, 1, 1, color));
        sp.getGutter().setBorderColor(color);

        return sp;
    }
}
