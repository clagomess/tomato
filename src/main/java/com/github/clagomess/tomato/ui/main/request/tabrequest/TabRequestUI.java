package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.service.HttpService;
import com.github.clagomess.tomato.ui.RequestSaveUI;
import com.github.clagomess.tomato.ui.component.ListenableTextFieldComponent;
import com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.keyvalue.KeyValueUI;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;
import com.github.clagomess.tomato.util.UIPublisherUtil;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@Getter
@Setter
public class TabRequestUI extends JPanel {
    private RequestDto requestDto;
    private final TabResponseUI tabResponseUi;

    private final JLabel lblRequestName = new JLabel("FOO - API / /api/get/test");
    private final JComboBox<HttpMethodEnum> cbHttpMethod = new JComboBox<>(
            HttpMethodEnum.values()
    );
    private final ListenableTextFieldComponent txtRequestUrl = new ListenableTextFieldComponent();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton(new FlatFileViewFloppyDriveIcon());
    private final ButtonGroup bgBodyType = new ButtonGroup();

    public TabRequestUI(RequestDto requestDto, TabResponseUI tabResponseUi){
        this.requestDto = requestDto;
        this.tabResponseUi = tabResponseUi;

        // layout definitions
        setLayout(new MigLayout("insets 10 5 10 5", "[grow,fill]", ""));

        // layout
        add(lblRequestName, "span");
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
        lblRequestName.setText(getLabelRequestName());
        cbHttpMethod.setSelectedItem(requestDto.getMethod());
        txtRequestUrl.setText(requestDto.getUrl());

        // listeners
        cbHttpMethod.addActionListener(l -> cbHttpMethodOnChange());
        txtRequestUrl.addOnChange(requestDto::setUrl);
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());
        UIPublisherUtil.getInstance().getSaveRequestFIList().add(dto -> { //@TODO: detruir quando remover tab
           if(dto.getId().equals(requestDto.getId())) return;
        });
    }

    private String getLabelRequestName(){
        CollectionDto collection = DataService.getInstance()
                .getCollectionByResquestId(requestDto.getId());
        if(collection == null){
            return requestDto.getName();
        }else {
            return String.format(
                    "%s - %s",
                    collection.getName(),
                    requestDto.getName()
            ); //@TODO: modify UI
        }
    }

    private void cbHttpMethodOnChange(){
        requestDto.setMethod((HttpMethodEnum) cbHttpMethod.getSelectedItem());
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
        CollectionDto collection = DataService.getInstance().getCollectionByResquestId(requestDto.getId());
        if(collection == null){
            new RequestSaveUI(requestDto);
            return;
        }

        try {
            DataService.getInstance().saveRequest(
                    DataService.getInstance().getCurrentWorkspace().getId(),
                    collection.getId(),
                    requestDto
            );
            collection.addOrReplaceRequest(requestDto);
            UIPublisherUtil.getInstance().notifySaveRequest(requestDto);
            //@TODO: atualizar na arvore
            //@TODO: atulizar na ui
        }catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }

        // @TODO: remove unsaved-hint
    }
}
