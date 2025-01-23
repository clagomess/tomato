package com.github.clagomess.tomato.ui.component.envtextfield;

import com.github.clagomess.tomato.dto.data.EnvironmentDto;
import com.github.clagomess.tomato.io.repository.EnvironmentRepository;
import com.github.clagomess.tomato.publisher.EnvironmentPublisher;
import com.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import com.github.clagomess.tomato.ui.ColorConstant;
import com.github.clagomess.tomato.util.ExecutorUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class EnvDocumentListener implements DocumentListener {
    @Getter
    private final List<EnvTextFieldOnChangeFI> onChangeList = new LinkedList<>();
    private final EnvironmentRepository environmentRepository = new EnvironmentRepository();

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final ExecutorService singleThreadExecutor = ExecutorUtil.getSingleThreadExecutor();

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

        new Thread(this::updateEnvMap, getClass().getSimpleName()).start();

        listenerUuid.add(workspaceSessionPublisher.getOnSave().addListener(event -> {
            updateEnvMap();
            updateEnvStyle();
            triggerOnChange();
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
        singleThreadExecutor.submit(() -> {
            updateEnvStyle();
            triggerOnChange();
        });
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        singleThreadExecutor.submit(() -> {
            updateEnvStyle();
            triggerOnChange();
        });
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
            envMap.getInjected().clear();
            if(document.getLength() == 0) return;

            String text = document.getText(0, document.getLength());
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
                log.debug("Token found: {}", token);

                document.setCharacterAttributes(
                        matcher.start(),
                        token.length(),
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
            envMap.reset();
            listenerUuid.forEach(uuid -> {
                environmentPublisher.getOnSave().removeListener(uuid);
            });

            Optional<EnvironmentDto> current = environmentRepository.getWorkspaceSessionEnvironment();
            if(current.isEmpty()) return;

            envMap.put(current.get());

            listenerUuid.add(environmentPublisher.getOnSave().addListener(current.get().getId(), e -> {
                updateEnvMap();
                updateEnvStyle();
            }));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }
}
