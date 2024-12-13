package com.github.clagomess.tomato.ui.main.request.left;

import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.HttpService;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSaveIcon;
import com.github.clagomess.tomato.ui.main.request.left.bodytype.keyvalue.KeyValueUI;
import com.github.clagomess.tomato.ui.main.request.right.ResponseTabContent;
import com.github.clagomess.tomato.ui.request.RequestSaveUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Getter
@Setter
public class RequestTabContentUI extends JPanel {
    private RequestHeadDto requestHeadDto;
    private final RequestDto requestDto;
    private final ResponseTabContent responseTabContent;

    private final RequestNameTextField txtRequestName = new RequestNameTextField();
    private final HttpMethodComboBox cbHttpMethod = new HttpMethodComboBox();
    private final ListenableTextField txtRequestUrl = new ListenableTextField();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton(new BxSaveIcon());
    private final ButtonGroup bgBodyType = new ButtonGroup();

    private final RequestPublisher requestPublisher = RequestPublisher.getInstance();
    private final RequestStagingMonitor requestStagingMonitor;

    public RequestTabContentUI(
            TabKey tabKey,
            RequestHeadDto requestHeadDto,
            RequestDto requestDto,
            ResponseTabContent responseTabContent
    ){
        this.requestHeadDto = requestHeadDto;
        this.requestDto = requestDto;
        this.responseTabContent = responseTabContent;
        this.requestStagingMonitor = new RequestStagingMonitor(
                tabKey,
                requestHeadDto,
                requestDto
        );

        // layout definitions
        setLayout(new MigLayout("insets 10 5 10 5", "[grow,fill]", ""));

        // layout
        add(txtRequestName, "span");
        add(new JSeparator(JSeparator.HORIZONTAL), "span");
        add(cbHttpMethod);
        add(txtRequestUrl, "width 100%");
        add(btnSendRequest);
        add(btnSaveRequest, "wrap");

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", new BodyUI(requestDto, requestStagingMonitor));
        //@TODO: add requestChangeDto
        //@TODO: add count
        tpRequest.addTab("Header", new KeyValueUI(requestDto.getHeaders()));
        //@TODO: add requestChangeDto
        //@TODO: add count
        tpRequest.addTab("Cookie", new KeyValueUI(requestDto.getCookies()));
        add(tpRequest, "span, height 100%");

        // set values
        txtRequestName.setText(requestHeadDto, requestDto);
        cbHttpMethod.setSelectedItem(requestDto.getMethod());
        txtRequestUrl.setText(requestDto.getUrl());

        // listeners
        cbHttpMethod.addActionListener(l -> {
            requestDto.setMethod(cbHttpMethod.getSelectedItem());
            requestStagingMonitor.setActualHashCode(requestDto);
        });
        txtRequestUrl.addOnChange(value -> {
            requestDto.setUrl(value);
            requestStagingMonitor.setActualHashCode(requestDto);
        });
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());
    }

    public void btnSendRequestAction(){
        btnSendRequest.setEnabled(false);
        responseTabContent.reset();

        new Thread(() -> {
            try {
                ResponseDto responseDto = HttpService.getInstance().perform(requestDto);
                responseTabContent.update(responseDto);
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            } finally {
                btnSendRequest.setEnabled(true);
            }
        }).start();
    }

    public void btnSaveRequestAction(){
        if(requestHeadDto == null){
            new RequestSaveUI(
                    this,
                    requestDto,
                    requestHead -> {
                        this.requestHeadDto = requestHead;
                        requestStagingMonitor.reset(requestDto);
                    }
            );
            return;
        }

        try {
            RequestDataService.getInstance()
                    .save(requestHeadDto.getPath(), requestDto);

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
            requestStagingMonitor.reset(requestDto);
        }catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }

    public void dispose(){
        txtRequestName.dispose();
    }
}
