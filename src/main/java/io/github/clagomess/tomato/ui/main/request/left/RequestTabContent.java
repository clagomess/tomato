package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.RequestDto;
import io.github.clagomess.tomato.dto.data.keyvalue.ContentTypeKeyValueItemDto;
import io.github.clagomess.tomato.dto.data.keyvalue.KeyValueItemDto;
import io.github.clagomess.tomato.dto.data.request.UrlParamDto;
import io.github.clagomess.tomato.dto.key.TabKey;
import io.github.clagomess.tomato.enums.BodyTypeEnum;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.publisher.RequestPublisher;
import io.github.clagomess.tomato.ui.component.CharsetComboBox;
import io.github.clagomess.tomato.ui.component.ComponentUtil;
import io.github.clagomess.tomato.ui.component.LoadingPane;
import io.github.clagomess.tomato.ui.main.request.keyvalue.KeyValue;
import io.github.clagomess.tomato.ui.main.request.keyvalue.KeyValueOptions;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;

@Getter
@Setter
public class RequestTabContent extends JPanel {
    private final TabKey tabKey;
    private final RequestDto requestDto;
    private final RequestStagingMonitor requestStagingMonitor;

    private final List<DisposableListener> disposables = new LinkedList<>();

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

            if(!(tpRequest.getComponentAt(selectedIndex) instanceof LoadingPane)) return;

            switch (selectedIndex){
                case 0: createTabQueryParams(tpRequest); break;
                case 1: createTabPathVariables(tpRequest); break;
                case 2: createTabBody(tpRequest); break;
                case 3: createTabHeaders(tpRequest); break;
                case 4: createTabCookies(tpRequest); break;
            }
        });

        setSelectedTabWithContent(tpRequest);

        add(tpRequest, "height 100%");

        addCookieSetListener(tpRequest);
    }

    private void createTabTitleQueryParams(JTabbedPane tabbedPane){
        var queryParamsTabTitle = new TabTitle(
                tabKey,
                "Query Params",
                () -> !requestDto.getUrlParam().getQuery().isEmpty()
        );

        tabbedPane.addTab(queryParamsTabTitle.getTitle(), new LoadingPane());
        tabbedPane.setTabComponentAt(0, queryParamsTabTitle);
    }

    private void createTabQueryParams(JTabbedPane tabbedPane){
        var queryParams = new KeyValue<>(
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
        disposables.add(queryParams);
    }

    private void createTabTitlePathVariables(JTabbedPane tabbedPane){
        var pathVariablesTabTitle = new TabTitle(
                tabKey,
                "Path Variables",
                () -> !requestDto.getUrlParam().getPath().isEmpty()
        );

        tabbedPane.addTab(pathVariablesTabTitle.getTitle(), new LoadingPane());
        tabbedPane.setTabComponentAt(1, pathVariablesTabTitle);
    }

    private void createTabPathVariables(JTabbedPane tabbedPane){
        var pathVariables = new KeyValue<>(
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
        disposables.add(pathVariables);
    }

    private void createTabTitleBody(JTabbedPane tabbedPane){
        var bodyTabTabTitle = new TabTitle(
                tabKey,
                "Body",
                () -> requestDto.getBody().getType() != BodyTypeEnum.NO_BODY
        );

        tabbedPane.addTab(bodyTabTabTitle.getTitle(), new LoadingPane());
        tabbedPane.setTabComponentAt(2, bodyTabTabTitle);
    }

    private void createTabBody(JTabbedPane tabbedPane){
        var body = new Body(
                requestDto.getBody(),
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(2, body);
        disposables.add(body);
    }

    private void createTabTitleHeaders(JTabbedPane tabbedPane){
        var headersTabTitle = new TabTitle(
                tabKey,
                "Headers",
                () -> !requestDto.getHeaders().isEmpty()
        );

        tabbedPane.addTab(headersTabTitle.getTitle(), new LoadingPane());
        tabbedPane.setTabComponentAt(3, headersTabTitle);
    }

    private void createTabHeaders(JTabbedPane tabbedPane){
        var headers = new KeyValue<>(
                requestDto.getHeaders(),
                KeyValueItemDto.class,
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(3, headers);
        disposables.add(headers);
    }

    private void createTabTitleCookies(JTabbedPane tabbedPane){
        var cookiesTabTitle = new TabTitle(
                tabKey,
                "Cookies",
                () -> !requestDto.getCookies().isEmpty()
        );

        tabbedPane.addTab(cookiesTabTitle.getTitle(), new LoadingPane());
        tabbedPane.setTabComponentAt(4, cookiesTabTitle);
    }

    private void createTabCookies(JTabbedPane tabbedPane){
        ComponentUtil.checkIsEventDispatchThread();

        var cookies = new KeyValue<>(
                requestDto.getCookies(),
                KeyValueItemDto.class,
                requestStagingMonitor
        );

        tabbedPane.setComponentAt(4, cookies);
        disposables.add(cookies);
    }

    private void addCookieSetListener(JTabbedPane tabbedPane){
        var uuid = RequestPublisher.getInstance()
                .getOnCookieSet()
                .addListener(tabKey, event -> invokeLater(() -> {
                    tabbedPane.setSelectedIndex(4);

                    if(tabbedPane.getComponentAt(4) instanceof KeyValue<?> cookies){
                        cookies.update(event.getKey(), event.getValue());
                    }
                }));

        disposables.add(() -> RequestPublisher.getInstance()
                .getOnCookieSet()
                .removeListener(uuid));
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

    public void dispose(){
        disposables.forEach(DisposableListener::dispose);
    }
}
