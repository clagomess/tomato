package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.http.HttpService;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextFieldOnChangeFI;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxBlockIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSendIcon;
import com.github.clagomess.tomato.ui.main.request.left.HttpMethodComboBox;
import com.github.clagomess.tomato.ui.main.request.left.RequestNameTextField;
import com.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import com.github.clagomess.tomato.ui.main.request.left.RequestTabContentUI;
import com.github.clagomess.tomato.ui.main.request.right.ResponseTabContent;
import com.github.clagomess.tomato.ui.request.RequestSaveUI;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

@Getter
public class RequestSplitPaneUI extends JPanel {
    private final TabKey key;
    private RequestHeadDto requestHeadDto;
    private final RequestDto requestDto;

    private final RequestNameTextField txtRequestName = new RequestNameTextField();
    private final JButton btnSaveRequest = new JButton(new BxSaveIcon()){{
        setToolTipText("Save");
    }};

    private final HttpMethodComboBox cbHttpMethod = new HttpMethodComboBox();
    private final EnvTextField txtRequestUrl = new EnvTextField();
    private final JButton btnSendRequest = new JButton("Send", new BxSendIcon());
    private final JButton btnCancelRequest = new JButton(new BxBlockIcon()){{
        setToolTipText("Cancel");
        setEnabled(false);
    }};

    private final RequestTabContentUI requestContent;
    private final ResponseTabContent responseContent;

    private final RequestStagingMonitor requestStagingMonitor;
    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();


    public RequestSplitPaneUI(
            RequestHeadDto requestHeadDto,
            RequestDto requestDto
    ) {
        this.key = new TabKey(requestDto.getId());
        this.requestHeadDto = requestHeadDto;
        this.requestDto = requestDto;
        this.requestStagingMonitor = new RequestStagingMonitor(
                key,
                requestHeadDto,
                requestDto
        );

        setLayout(new MigLayout(
                "insets 5 5 10 10",
                "[grow,fill]"
        ));

        var paneRequestName = new JPanel(new MigLayout(
                "insets 0 0 0 0",
                "[grow,fill][]",
                ""
        ));
        paneRequestName.add(txtRequestName, "width 300::100%");
        paneRequestName.add(btnSaveRequest);
        add(paneRequestName, "wrap");

        var paneUrl = new JPanel(new MigLayout(
                "insets 0 0 5 0",
                "[][grow,fill][]",
                ""
        ));
        paneUrl.add(cbHttpMethod);
        paneUrl.add(txtRequestUrl);
        paneUrl.add(btnSendRequest);
        paneUrl.add(btnCancelRequest);
        add(paneUrl, "width 300::100%, wrap");

        // set values
        txtRequestName.setText(requestHeadDto, requestDto);
        cbHttpMethod.setSelectedItem(requestDto.getMethod());
        txtRequestUrl.setText(requestDto.getUrl());

        // listeners
        cbHttpMethod.addActionListener(l -> {
            requestDto.setMethod(cbHttpMethod.getSelectedItem());
            requestStagingMonitor.update();
        });

        EnvTextFieldOnChangeFI txtRequestUrlChange = value -> {
            requestDto.setUrl(value);
            requestStagingMonitor.update();
            requestPublisher.getOnUrlChange().publish(key, value);
        };

        txtRequestUrl.addOnChange(txtRequestUrlChange);
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());

        requestPublisher.getOnUrlParamChange().addListener(key, url -> {
            txtRequestUrl.getOnChangeList().remove(txtRequestUrlChange);
            txtRequestUrl.setText(url);
            SwingUtilities.invokeLater(() -> txtRequestUrl.getOnChangeList().add(txtRequestUrlChange));
        });

        // # REQUEST / RESPONSE
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#616365")));
        splitPane.setDividerLocation(0.5);

        this.requestContent = new RequestTabContentUI(
                key,
                requestDto,
                requestStagingMonitor
        );

        this.responseContent = new ResponseTabContent();

        splitPane.setLeftComponent(requestContent);
        splitPane.setRightComponent(responseContent);
        add(splitPane, "height 100%");
    }

    public void btnSendRequestAction(){
        btnSendRequest.setEnabled(false);
        btnCancelRequest.setEnabled(true);
        responseContent.reset();

        Arrays.stream(btnCancelRequest.getActionListeners())
                .forEach(btnCancelRequest::removeActionListener);

        Thread requestThread = new Thread(() -> {
            try {
                ResponseDto responseDto = new HttpService(requestDto).perform();
                responseContent.update(responseDto);
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            } finally {
                btnSendRequest.setEnabled(true);
                btnCancelRequest.setEnabled(false);
            }
        });

        requestThread.start();

        btnCancelRequest.addActionListener(l -> {
            btnCancelRequest.setEnabled(false);
            requestThread.interrupt();
        });
    }

    public void btnSaveRequestAction(){
        if(requestHeadDto == null){
            new RequestSaveUI(
                    this,
                    requestDto,
                    requestHead -> {
                        this.requestHeadDto = requestHead;
                        requestStagingMonitor.reset();
                    }
            );
            return;
        }

        new WaitExecution(this, btnSaveRequest, () -> {
            requestRepository.save(requestHeadDto.getPath(), requestDto);

            RequestMapper.INSTANCE.toRequestHead(
                    requestHeadDto,
                    requestDto
            );

            var key = new RequestPublisher.RequestKey(
                    requestHeadDto.getParent().getId(),
                    requestHeadDto.getId()
            );

            requestPublisher.getOnUpdate().publish(key, requestHeadDto);
            requestPublisher.getOnSave().publish(requestHeadDto.getId(), requestHeadDto);
            requestStagingMonitor.reset();
        }).execute();
    }

    public void dispose(){
        txtRequestName.dispose();
        txtRequestUrl.dispose();
        requestContent.dispose();
    }
}
