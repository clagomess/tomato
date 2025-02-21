package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import com.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import com.github.clagomess.tomato.dto.data.request.UrlParamDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.CharsetComboBox;
import com.github.clagomess.tomato.ui.main.request.keyvalue.KeyValue;
import com.github.clagomess.tomato.ui.main.request.keyvalue.KeyValueOptions;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static javax.swing.SwingUtilities.invokeLater;

@Getter
@Setter
public class RequestTabContent extends JPanel {
    private final TabKey tabKey;
    private final RequestDto requestDto;
    private final RequestStagingMonitor requestStagingMonitor;

    private KeyValue<ContentTypeKeyValueItemDto> queryParams;
    private KeyValue<KeyValueItemDto> pathVariables;
    private Body body;
    private KeyValue<KeyValueItemDto> headers;
    private KeyValue<KeyValueItemDto> cookies;

    public RequestTabContent(
            TabKey tabKey,
            RequestDto requestDto,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.tabKey = tabKey;
        this.requestDto = requestDto;
        this.requestStagingMonitor = requestStagingMonitor;

        // layout definitions
        setLayout(new MigLayout(
                "insets 5 0 0 5",
                "[grow,fill]"
        ));

        JTabbedPane tpRequest = new JTabbedPane();
        createTabTitleQueryParams(tpRequest);
        createTabTitlePathVariables(tpRequest);
        createTabTitleBody(tpRequest);
        createTabTitleHeaders(tpRequest);
        createTabTitleCookies(tpRequest);
        tpRequest.setSelectedIndex(-1);

        tpRequest.addChangeListener(e -> {
            int selectedIndex = tpRequest.getSelectedIndex();

            if(!(tpRequest.getComponentAt(selectedIndex) instanceof JLabel)) return;

            invokeLater(() -> {
                switch (selectedIndex){
                    case 0: createTabQueryParams(tpRequest); break;
                    case 1: createTabPathVariables(tpRequest); break;
                    case 2: createTabBody(tpRequest); break;
                    case 3: createTabHeaders(tpRequest); break;
                    case 4: createTabCookies(tpRequest); break;
                }
            });
        });

        setSelectedTabWithContent(tpRequest);

        add(tpRequest, "height 100%");

        addCookieSetListener();

        // @TODO: update tab title when modify content
    }

    private void createTabTitleQueryParams(JTabbedPane tabbedPane){
        var queryParamsTabTitle = new TabTitle(
                "Query Params",
                !requestDto.getUrlParam().getQuery().isEmpty()
        );

        tabbedPane.addTab(queryParamsTabTitle.getTitle(), new JLabel("loading"));
        tabbedPane.setTabComponentAt(0, queryParamsTabTitle);
    }

    private void createTabQueryParams(JTabbedPane tabbedPane){
        queryParams = new KeyValue<>(
                requestDto.getUrlParam().getQuery(),
                ContentTypeKeyValueItemDto.class,
                requestStagingMonitor,
                KeyValueOptions.builder()
                        .charsetComboBox(getCharsetComboBox(
                                requestDto.getUrlParam(),
                                requestStagingMonitor
                        ))
                        .build()
        );

        tabbedPane.setComponentAt(0, queryParams);
    }

    private void createTabTitlePathVariables(JTabbedPane tabbedPane){
        var pathVariablesTabTitle = new TabTitle(
                "Path Variables",
                !requestDto.getUrlParam().getPath().isEmpty()
        );

        tabbedPane.addTab(pathVariablesTabTitle.getTitle(), new JLabel("loading"));
        tabbedPane.setTabComponentAt(1, pathVariablesTabTitle);
    }

    private void createTabPathVariables(JTabbedPane tabbedPane){
        pathVariables = new KeyValue<>(
                requestDto.getUrlParam().getPath(),
                KeyValueItemDto.class,
                requestStagingMonitor,
                KeyValueOptions.builder()
                        .charsetComboBox(getCharsetComboBox(
                                requestDto.getUrlParam(),
                                requestStagingMonitor
                        ))
                        .build()
        );

        tabbedPane.setComponentAt(1, pathVariables);
    }

    private void createTabTitleBody(JTabbedPane tabbedPane){
        var bodyTabTabTitle = new TabTitle(
                "Body",
                requestDto.getBody().getType() != BodyTypeEnum.NO_BODY
        );

        tabbedPane.addTab(bodyTabTabTitle.getTitle(), new JLabel("loading"));
        tabbedPane.setTabComponentAt(2, bodyTabTabTitle);
    }

    private void createTabBody(JTabbedPane tabbedPane){
        body = new Body(
                requestDto.getBody(),
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(2, body);
    }

    private void createTabTitleHeaders(JTabbedPane tabbedPane){
        var headersTabTitle = new TabTitle(
                "Headers",
                !requestDto.getHeaders().isEmpty()
        );

        tabbedPane.addTab(headersTabTitle.getTitle(), new JLabel("loading"));
        tabbedPane.setTabComponentAt(3, headersTabTitle);
    }

    private void createTabHeaders(JTabbedPane tabbedPane){
        headers = new KeyValue<>(
                requestDto.getHeaders(),
                KeyValueItemDto.class,
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(3, headers);
    }

    private void createTabTitleCookies(JTabbedPane tabbedPane){
        var cookiesTabTitle = new TabTitle(
                "Cookies",
                !requestDto.getCookies().isEmpty()
        );

        tabbedPane.addTab(cookiesTabTitle.getTitle(), new JLabel("loading"));
        tabbedPane.setTabComponentAt(4, cookiesTabTitle);
    }

    private void createTabCookies(JTabbedPane tabbedPane){
        cookies = new KeyValue<>(
                requestDto.getCookies(),
                KeyValueItemDto.class,
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(4, cookies);
    }

    private void setSelectedTabWithContent(JTabbedPane tabbedPane){
        for (var i = 0; i < tabbedPane.getTabCount(); i++) {
            if(tabbedPane.getTabComponentAt(i) instanceof TabTitle tabTitle){
                if(!tabTitle.isHasContent()) continue;
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        tabbedPane.setSelectedIndex(0);
    }

    private CharsetComboBox getCharsetComboBox(
            UrlParamDto urlParam,
            RequestStagingMonitor requestStagingMonitor
    ){
        CharsetComboBox comboBox = new CharsetComboBox();
        comboBox.setSelectedItem(urlParam.getCharset());
        comboBox.addActionListener(l -> {
            urlParam.setCharset(comboBox.getSelectedItem());
            requestStagingMonitor.update();
        });

        return comboBox;
    }

    private UUID cookieListenerUuid = null;
    private void addCookieSetListener(){
        cookieListenerUuid = RequestPublisher.getInstance()
                .getOnCookieSet()
                .addListener(tabKey, event -> {
                    if (cookies != null) {
                        cookies.update(event.getKey(), event.getValue());
                        return;
                    }

                    Optional<KeyValueItemDto> cookie = requestDto.getCookies().stream()
                            .filter(item -> Objects.equals(event.getKey(), item.getKey()))
                            .findFirst();

                    if (cookie.isPresent()) {
                        cookie.get().setValue(event.getValue());
                    } else {
                        requestDto.getCookies().add(new KeyValueItemDto(
                                event.getKey(),
                                event.getValue()
                        ));
                    }

                    requestStagingMonitor.update();
                });
    }

    public void dispose(){
        if(queryParams != null) queryParams.dispose();
        if(pathVariables != null) pathVariables.dispose();
        if(body != null) body.dispose();
        if(headers != null) headers.dispose();
        if(cookies != null) cookies.dispose();

        if(cookieListenerUuid != null){
            RequestPublisher.getInstance()
                    .getOnCookieSet()
                    .removeListener(cookieListenerUuid);
        }
    }
}
