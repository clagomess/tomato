package io.github.clagomess.tomato.ui.component.envtextfield;

import io.github.clagomess.tomato.publisher.EnvironmentPublisher;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.publisher.WorkspaceSessionPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Slf4j
class EnvDocumentListener implements DocumentListener {
    @Getter
    private final List<EnvTextFieldOnChangeFI> onChangeList = new LinkedList<>();

    private final List<UUID> listenerUuid = new ArrayList<>(2);
    private final WorkspaceSessionPublisher workspaceSessionPublisher = WorkspaceSessionPublisher.getInstance();
    private final EnvironmentPublisher environmentPublisher = EnvironmentPublisher.getInstance();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();

    private final StyledDocument document;
    private final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();

    private final List<StyleMap> styleMapList = new ArrayList<>(2);

    public EnvDocumentListener(
            @NotNull StyledDocument document,
            @Nullable EnvTextfieldOptions.PathVar pathVar
    ) {
        this.document = document;
        this.styleMapList.add(new EnvStyleMap());

        listenerUuid.add(workspaceSessionPublisher.getOnChange().addListener(event ->
            ForkJoinPool.commonPool().submit(this::updateStyle)
        ));

        listenerUuid.add(environmentPublisher.getOnChange().addListener(e ->
            ForkJoinPool.commonPool().submit(this::updateStyle)
        ));

        if(pathVar != null){
            var pathVarMap = new PathVarStyleMap(pathVar);
            this.styleMapList.add(pathVarMap);

            listenerUuid.add(requestPublisher.getOnPathVarChange().addListener(pathVar.tabKey(), e -> {
                pathVarMap.setUrlPathParam(e);
                ForkJoinPool.commonPool().submit(this::updateStyle);
            }));
        }
    }

    public void dispose() {
        listenerUuid.forEach(uuid -> {
            workspaceSessionPublisher.getOnChange().removeListener(uuid);
            environmentPublisher.getOnChange().removeListener(uuid);
            requestPublisher.getOnPathVarChange().removeListener(uuid);
        });
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        ForkJoinPool.commonPool().submit(this::updateStyle);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        ForkJoinPool.commonPool().submit(this::updateStyle);
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

    protected synchronized void updateStyle(){
        try {
            styleMapList.forEach(StyleMap::clearInjected);
            String text = getText();
            if(StringUtils.isBlank(text)) return;

            document.setCharacterAttributes(
                    0,
                    document.getLength(),
                    defaultStyle,
                    true
            );

            for(var item : styleMapList){
                item.update(document, text);
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

    public Map<String, String> getInjected(){
        var result = new HashMap<String, String>();
        styleMapList.forEach(item -> result.putAll(item.getInjected()));
        return result;
    }
}
