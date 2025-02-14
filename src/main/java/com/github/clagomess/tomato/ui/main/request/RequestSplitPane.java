package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.io.http.HttpService;
import com.github.clagomess.tomato.io.repository.RequestRepository;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.publisher.base.PublisherEvent;
import com.github.clagomess.tomato.publisher.key.RequestKey;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxBlockIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxGlobeIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSendIcon;
import com.github.clagomess.tomato.ui.main.request.left.*;
import com.github.clagomess.tomato.ui.main.request.right.ResponseTabContent;
import com.github.clagomess.tomato.ui.request.RequestSaveFrame;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

import static com.github.clagomess.tomato.publisher.base.EventTypeEnum.UPDATED;
import static javax.swing.SwingUtilities.invokeLater;

@Getter
public class RequestSplitPane extends JPanel {
    private final TabKey key;
    private RequestHeadDto requestHeadDto;
    private final RequestDto requestDto;

    private final RequestNameTextField txtRequestName = new RequestNameTextField();
    private final JButton btnViewRenderedUrl = new JButton(new BxGlobeIcon()){{
       setToolTipText("View Rendered Url");
    }};
    private final JButton btnSaveRequest = new JButton(new BxSaveIcon()){{
        setToolTipText("Save");
    }};

    private final HttpMethodComboBox cbHttpMethod = new HttpMethodComboBox();
    private final EnvTextField txtRequestUrl = new EnvTextField(EnvTextfieldOptions.builder().build());
    private final JButton btnSendRequest = new JButton("Send", new BxSendIcon());
    private final JButton btnCancelRequest = new JButton(new BxBlockIcon(Color.RED)){{
        setToolTipText("Cancel");
        setEnabled(false);
    }};

    private final RequestTabContent requestContent;
    private final ResponseTabContent responseContent;

    private final RequestStagingMonitor requestStagingMonitor;
    private final RequestRepository requestRepository = new RequestRepository();
    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();


    public RequestSplitPane(
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
                "insets 5",
                "[grow,fill]"
        ));

        var paneRequestName = new JPanel(new MigLayout(
                "insets 2",
                "[grow,fill][][]"
        ));
        paneRequestName.add(txtRequestName, "width 300::100%");
        paneRequestName.add(btnViewRenderedUrl);
        paneRequestName.add(btnSaveRequest);
        add(paneRequestName, "wrap");

        var paneUrl = new JPanel(new MigLayout(
                "insets 2",
                "[][grow,fill][]"
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

        txtRequestUrl.addOnChange(value -> {
            requestDto.setUrl(value);
            requestStagingMonitor.update();
        });
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnViewRenderedUrl.addActionListener(l -> btnViewRenderedUrlAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());

        // # REQUEST / RESPONSE
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new MatteBorder(1, 0, 0, 0, ColorConstant.GRAY));
        splitPane.setDividerLocation(580);
        splitPane.setResizeWeight(0.3);
        splitPane.setContinuousLayout(true);

        this.requestContent = new RequestTabContent(
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
                invokeLater(() -> new ExceptionDialog(this, e));
            } finally {
                invokeLater(() -> {
                    btnSendRequest.setEnabled(true);
                    btnCancelRequest.setEnabled(false);
                });
            }
        }, "request-perform");

        requestThread.start();

        btnCancelRequest.addActionListener(l -> {
            btnCancelRequest.setEnabled(false);
            requestThread.interrupt();
        });
    }

    public void btnViewRenderedUrlAction(){
        new WaitExecution(
                btnViewRenderedUrl,
                () -> new ViewRenderedUrlFrame(this, requestDto)
        ).execute();
    }

    public void btnSaveRequestAction(){
        if(requestHeadDto == null){
            new RequestSaveFrame(
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

            requestPublisher.getOnChange().publish(
                    new RequestKey(requestHeadDto),
                    new PublisherEvent<>(UPDATED, requestHeadDto)
            );

            requestStagingMonitor.reset();
        }).execute();
    }

    public void dispose(){
        txtRequestName.dispose();
        txtRequestUrl.dispose();
        requestContent.dispose();
    }
}
