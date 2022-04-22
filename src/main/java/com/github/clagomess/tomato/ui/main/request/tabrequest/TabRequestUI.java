package com.github.clagomess.tomato.ui.main.request.tabrequest;

import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.service.HttpService;
import com.github.clagomess.tomato.ui.main.request.tabrequest.bodytype.*;
import com.github.clagomess.tomato.ui.main.request.tabresponse.TabResponseUI;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;

@Getter
@Setter
public class TabRequestUI extends JPanel {
    private final RequestDto requestDto;
    private final TabResponseUI tabResponseUi;

    private final JLabel lblRequestName = new JLabel("FOO - API / /api/get/test");
    private final JComboBox<HttpMethodEnum> cbHttpMethod = new JComboBox<>();
    private final JTextField txtRequestUrl = new JTextField();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton(new FlatFileViewFloppyDriveIcon());
    private final RequestKeyValueTableUI tblHeader = new RequestKeyValueTableUI("Header", "Value");
    private BodyTypeUI bodyTypeUI;

    public TabRequestUI(RequestDto requestDto, TabResponseUI tabResponseUi){
        this.requestDto = requestDto;
        this.tabResponseUi = tabResponseUi;

        // layout definitions
        setLayout(new MigLayout("insets 10 5 10 5", "[grow,fill]", ""));

        add(lblRequestName, "span");
        add(new JSeparator(JSeparator.HORIZONTAL), "span");
        add(cbHttpMethod);
        add(txtRequestUrl, "width 100%");
        add(btnSendRequest);
        add(btnSaveRequest, "wrap");

        JTabbedPane tpRequest = new JTabbedPane();
        tpRequest.addTab("Body", getBody());
        tpRequest.addTab("Header", tblHeader);

        add(tpRequest, "span, height 100%");

        // data handling
        Arrays.stream(HttpMethodEnum.values()).forEach(cbHttpMethod::addItem);
        fillUIFromRequestDto();
        btnSendRequest.addActionListener(l -> btnSendRequestAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());
    }

    private void fillUIFromRequestDto(){
        CollectionDto collection = DataService.getInstance().getCollectionByResquestId(requestDto.getId());
        if(collection == null){
            lblRequestName.setText(requestDto.getName());
        }else {
            lblRequestName.setText(String.format("%s - %s", collection.getName(), requestDto.getName())); //@TODO: modify UI
        }

        cbHttpMethod.setSelectedItem(requestDto.getMethod());
        txtRequestUrl.setText(requestDto.getUrl());
    }

    private RequestDto getNewDtoFromUI(){
        RequestDto dto = new RequestDto(); //@TODO: clone
        dto.setId(requestDto.getId());
        dto.setName(lblRequestName.getText());
        dto.setMethod((HttpMethodEnum) cbHttpMethod.getSelectedItem());
        dto.setUrl(txtRequestUrl.getText());
        dto.setHeaders(tblHeader.getNewListDtoFromUI());
        dto.setCookies(null); //@TODO: implements getNewDtoFromUI cookies
        dto.setBody(bodyTypeUI.getNewDtoFromUI());

        return dto;
    }

    public JPanel getBody(){
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new MigLayout("insets 5 0 0 0", "[grow, fill]", ""));

        ButtonGroup bgBodyType = new ButtonGroup();
        JPanel pRadioBodyType = new JPanel(new MigLayout("insets 5 0 5 0"));

        Arrays.stream(BodyTypeEnum.values()).forEach(item -> {
            JRadioButton rbBodyType = new JRadioButton(item.getDescription());
            rbBodyType.setSelected(item == BodyTypeEnum.NO_BODY);
            rbBodyType.addActionListener(l -> {
                bodyTypeUI = getBodyType(item);
                bodyPanel.remove(1);
                bodyPanel.add((JComponent) bodyTypeUI, "height 100%");
                bodyPanel.revalidate();
                bodyPanel.repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        bodyTypeUI = getBodyType(BodyTypeEnum.NO_BODY);
        bodyPanel.add(pRadioBodyType, "wrap");
        bodyPanel.add((JComponent) bodyTypeUI, "height 100%");

        return bodyPanel;
    }

    private BodyTypeUI getBodyType(BodyTypeEnum bodyType){
        switch (bodyType){
            case MULTIPART_FORM:
                return new MultiPartFormUI();
            case URL_ENCODED_FORM:
                return new UrlEncodedFormUI("Key", "Value");
            case RAW:
                return new RawBodyUI();
            case BINARY:
                return new BinaryUI();
            default:
                return new NoBodyUI();
        }
    }

    public void btnSendRequestAction(){
        btnSendRequest.setEnabled(false);
        tabResponseUi.reset();
        RequestDto dto = getNewDtoFromUI();

        new Thread(() -> {
            try {
                ResponseDto responseDto = new HttpService().perform(dto);
                tabResponseUi.update(responseDto);
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            } finally {
                btnSendRequest.setEnabled(true);
            }
        }).start();
    }

    public void btnSaveRequestAction(){
        RequestDto dto = getNewDtoFromUI();
        CollectionDto collection = DataService.getInstance().getCollectionByResquestId(requestDto.getId());
        if(collection == null){
            // @TODO: prompt diolog when new
            return;
        }

        try {
            DataService.getInstance().saveRequest(
                    DataService.getInstance().getCurrentWorkspace().getId(),
                    collection.getId(),
                    dto
            );
            //@TODO: atualizar na arvore
            //@TODO: atulizar na ui
        }catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }

        // @TODO: remove unsaved-hint
    }
}
