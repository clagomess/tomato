package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.github.clagomess.tomato.dto.CollectionTreeDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.mapper.RequestMapper;
import com.github.clagomess.tomato.publisher.RequestPublisher;
import com.github.clagomess.tomato.service.HttpService;
import com.github.clagomess.tomato.service.RequestDataService;
import com.github.clagomess.tomato.ui.component.DialogFactory;
import com.github.clagomess.tomato.ui.component.ListenableTextField;
import com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.keyvalue.KeyValueUI;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;
import com.github.clagomess.tomato.ui.request.RequestSaveUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Getter
@Setter
public class TabRequestUI extends JPanel {
    private final CollectionTreeDto.Request requestHeadDto;
    private final RequestDto requestDto;
    private final TabResponseUI tabResponseUi;

    private final RequestNameTextField txtRequestName = new RequestNameTextField();
    private final JComboBox<HttpMethodEnum> cbHttpMethod = new JComboBox<>(
            HttpMethodEnum.values()
    );
    private final ListenableTextField txtRequestUrl = new ListenableTextField();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton(new FlatFileViewFloppyDriveIcon());
    private final ButtonGroup bgBodyType = new ButtonGroup();

    public TabRequestUI(
            CollectionTreeDto.Request requestHeadDto,
            RequestDto requestDto,
            TabResponseUI tabResponseUi
    ){
        this.requestHeadDto = requestHeadDto;
        this.requestDto = requestDto;
        this.tabResponseUi = tabResponseUi;

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
        tpRequest.addTab("Body", new BodyUI(requestDto));
        tpRequest.addTab("Header", new KeyValueUI(requestDto.getHeaders()));
        tpRequest.addTab("Cookie", new KeyValueUI(requestDto.getCookies()));
        add(tpRequest, "span, height 100%");

        // set values
        txtRequestName.setText(requestHeadDto, requestDto);
        cbHttpMethod.setSelectedItem(requestDto.getMethod());
        txtRequestUrl.setText(requestDto.getUrl());

        // listeners
        cbHttpMethod.addActionListener(l -> requestDto.setMethod((HttpMethodEnum) cbHttpMethod.getSelectedItem()));
        txtRequestUrl.addOnChange(requestDto::setUrl);
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());
    }

    public void btnSendRequestAction(){
        btnSendRequest.setEnabled(false);
        tabResponseUi.reset();

        new Thread(() -> {
            try {
                ResponseDto responseDto = HttpService.getInstance().perform(requestDto);
                tabResponseUi.update(responseDto);
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            } finally {
                btnSendRequest.setEnabled(true);
            }
        }).start();
    }

    public void btnSaveRequestAction(){
        if(requestHeadDto == null){
            new RequestSaveUI(requestDto);
            return;
        }

        try {
            RequestDataService.getInstance()
                    .save(requestHeadDto.getPath(), requestDto);

            RequestMapper.INSTANCE.toRequestHead(
                    requestHeadDto,
                    requestDto
            );

            RequestPublisher.getInstance()
                    .getOnSave()
                    .publish(requestHeadDto);
        }catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }
    }
}
