package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class EnvDocumentListener implements DocumentListener {
    @Getter
    private final List<EnvTextFieldOnChangeFI> onChangeList = new LinkedList<>();

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    private final StyledDocument document;
    private final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envFilledStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envNotFilledStyle = new SimpleAttributeSet();
    protected final Pattern patternEnv = Pattern.compile("(\\{\\{.+?\\}\\}?)");

    @Getter
    private final EnvMap envMap = new EnvMap();

    public EnvDocumentListener(StyledDocument document) {
        this.document = document;

        StyleConstants.setForeground(envFilledStyle, ColorConstant.GREEN);
        StyleConstants.setForeground(envNotFilledStyle, ColorConstant.RED);

        listenerUuid.add(workspaceSessionPublisher.getOnChange().addListener(event ->
            ForkJoinPool.commonPool().submit(this::updateEnvStyle)
        ));

        listenerUuid.add(environmentPublisher.getOnChange().addListener(e ->
            ForkJoinPool.commonPool().submit(this::updateEnvStyle)
        ));
    }

    public void dispose() {
        listenerUuid.forEach(uuid -> {
            workspaceSessionPublisher.getOnChange().removeListener(uuid);
            environmentPublisher.getOnChange().removeListener(uuid);
        });
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        ForkJoinPool.commonPool().submit(this::updateEnvStyle);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        ForkJoinPool.commonPool().submit(this::updateEnvStyle);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {}

    protected String getText() {
        try {
            if(document.getLength() == 0) return null;
            return document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    protected synchronized void updateEnvStyle(){
        try {
            envMap.getInjected().clear();
            String text = getText();
            if(StringUtils.isBlank(text)) return;

            document.setCharacterAttributes(
                    0,
                    document.getLength(),
                    defaultStyle,
                    true
            );

            Matcher matcher = patternEnv.matcher(text);
            while (matcher.find()) {
                String token = matcher.group();
                if(log.isDebugEnabled()) log.debug("Token found: {}", token);

                document.setCharacterAttributes(
                        matcher.start(),
                        token.length(),
                        envMap.containsKey(token) ? envFilledStyle : envNotFilledStyle,
                        true
                );
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            String text = getText();
            onChangeList.forEach(ch ->
                    ForkJoinPool.commonPool().submit(() -> ch.change(text))
            );
        }
    }
}
