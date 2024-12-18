package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.ui.ColorConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class EnvDocumentListener implements DocumentListener {
    @Getter
    private final List<EnvTextFieldOnChangeFI> onChangeList = new LinkedList<>();
    private final StyledDocument document;

    private final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envFilledStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envNotFilledStyle = new SimpleAttributeSet();
    protected final Pattern patternEnv = Pattern.compile("(\\{\\{.+?\\}\\}?)");

    public EnvDocumentListener(StyledDocument document) {
        this.document = document;

        StyleConstants.setForeground(envFilledStyle, ColorConstant.GREEN);
        StyleConstants.setForeground(envNotFilledStyle, ColorConstant.RED);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        triggerOnChange();
        SwingUtilities.invokeLater(this::updateEnvStyle);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        triggerOnChange();
        SwingUtilities.invokeLater(this::updateEnvStyle);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {}

    public void triggerOnChange() {
        try {
            String text = document.getText(0, document.getLength());
            onChangeList.forEach(ch -> ch.change(text));
        } catch (BadLocationException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateEnvStyle(){
        try {
            document.setCharacterAttributes(
                    0,
                    document.getLength(),
                    defaultStyle,
                    true
            );

            String text = document.getText(0, document.getLength());
            Matcher matcher = patternEnv.matcher(text);

            while (matcher.find()) {
                log.debug("Token found: {}", matcher.group());

                document.setCharacterAttributes(
                        matcher.start(),
                        matcher.end(),
                        envNotFilledStyle, // @TODO: implements env check
                        true
                );
            }

        } catch (BadLocationException e) {
            log.error(e.getMessage(), e);
        }
    }
}
