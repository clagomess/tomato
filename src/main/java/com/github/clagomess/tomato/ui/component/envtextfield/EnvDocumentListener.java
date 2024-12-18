package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.service.EnvironmentDataService;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class EnvDocumentListener implements DocumentListener {
    @Getter
    private final List<EnvTextFieldOnChangeFI> onChangeList = new LinkedList<>();
    private final EnvironmentDataService environmentDataService = new EnvironmentDataService();

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();

    private final StyledDocument document;
    private final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envFilledStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet envNotFilledStyle = new SimpleAttributeSet();
    protected final Pattern patternEnv = Pattern.compile("(\\{\\{.+?\\}\\}?)");
    private final Map<String, String> envMap = new HashMap<>();

    public EnvDocumentListener(StyledDocument document) {
        this.document = document;

        StyleConstants.setForeground(envFilledStyle, ColorConstant.GREEN);
        StyleConstants.setForeground(envNotFilledStyle, ColorConstant.RED);

        SwingUtilities.invokeLater(() -> {
            updateEnvMap();
            updateEnvStyle();
        });

        listenerUuid.add(workspaceSessionPublisher.getOnSave().addListener(event -> {
            updateEnvMap();
            updateEnvStyle();
        }));
    }

    public void dispose() {
        listenerUuid.forEach(uuid -> {
            workspaceSessionPublisher.getOnSave().removeListener(uuid);
            environmentPublisher.getOnSave().removeListener(uuid);
        });
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
                String token = matcher.group();
                log.debug("Token found: {}", token);

                document.setCharacterAttributes(
                        matcher.start(),
                        matcher.end(),
                        envMap.containsKey(token) ? envFilledStyle : envNotFilledStyle,
                        true
                );
            }

        } catch (BadLocationException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateEnvMap(){
        try {
            envMap.clear();
            listenerUuid.forEach(uuid -> {
                environmentPublisher.getOnSave().removeListener(uuid);
            });

            Optional<EnvironmentDto> current = environmentDataService.getWorkspaceSessionEnvironment();
            if(current.isEmpty()) return;

            current.get().getEnvs().forEach(env -> {
                envMap.put("{{" + env.getKey() + "}}", env.getValue());
            });

            listenerUuid.add(environmentPublisher.getOnSave().addListener(current.get().getId(), e -> {
                updateEnvMap();
                updateEnvStyle();
            }));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
