package com.github.clagomess.tomato.ui.request.tabrequest;

import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.github.clagomess.tomato.dto.CollectionDto;
import com.github.clagomess.tomato.dto.RequestDto;
import com.github.clagomess.tomato.dto.ResponseDto;
import com.github.clagomess.tomato.enums.BodyTypeEnum;
import com.github.clagomess.tomato.enums.HttpMethodEnum;
import com.github.clagomess.tomato.factory.DialogFactory;
import com.github.clagomess.tomato.service.DataService;
import com.github.clagomess.tomato.service.HttpService;
import com.github.clagomess.tomato.ui.request.tabrequest.bodytype.MultiPartFormUI;
import com.github.clagomess.tomato.ui.request.tabrequest.bodytype.RawBodyUI;
import com.github.clagomess.tomato.ui.request.tabresponse.TabResponseUi;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;

@Getter
@Setter
public class TabRequestUi extends JPanel {
    private final RequestDto requestDto;
    private final TabResponseUi tabResponseUi;

    private final JLabel lblRequestName = new JLabel("FOO - API / /api/get/test");
    private final JComboBox<HttpMethodEnum> cbHttpMethod = new JComboBox<>();
    private final JTextField txtRequestUrl = new JTextField();
    private final JButton btnSendRequest = new JButton("Send");
    private final JButton btnSaveRequest = new JButton(new FlatFileViewFloppyDriveIcon());
    private final RequestKeyValueTableUI tblHeader = new RequestKeyValueTableUI("Header", "Value");

    public TabRequestUi(RequestDto requestDto, TabResponseUi tabResponseUi){
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

    private void fillRequestDtoFromUI(){
        requestDto.setUrl(txtRequestUrl.getText()); // //@TODO: não fazer dessa forma, pois está atualizando na arvore. criar um novo objeto
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
                bodyPanel.remove(1);
                bodyPanel.add(getBodyType(item), "height 100%");
                bodyPanel.revalidate();
                bodyPanel.repaint();
            });

            bgBodyType.add(rbBodyType);
            pRadioBodyType.add(rbBodyType);
        });

        bodyPanel.add(pRadioBodyType, "wrap");
        bodyPanel.add(getBodyType(BodyTypeEnum.NO_BODY), "height 100%");

        return bodyPanel;
    }

    private JComponent getBodyType(BodyTypeEnum bodyType){
        switch (bodyType){
            case MULTIPART_FORM:
                return new MultiPartFormUI();
            case URL_ENCODED_FORM:
                return new RequestKeyValueTableUI("Key", "Value");
            case RAW:
                return new RawBodyUI();
            case BINARY:
                return new FileChooserUI();
            default:
                return new JPanel();
        }
    }

    public void btnSendRequestAction(){
        btnSendRequest.setEnabled(false);
        fillRequestDtoFromUI();
        tabResponseUi.reset();

        new Thread(() -> {
            try {
                ResponseDto responseDto = new HttpService().perform(requestDto);
                tabResponseUi.update(responseDto);
            } catch (Throwable e) {
                DialogFactory.createDialogException(this, e);
            } finally {
                btnSendRequest.setEnabled(true);
            }
        }).start();
    }

    public void btnSaveRequestAction(){
        fillRequestDtoFromUI();
        CollectionDto collection = DataService.getInstance().getCollectionByResquestId(requestDto.getId());
        if(collection == null){
            // @TODO: prompt diolog when new
            return;
        }

        try {
            DataService.getInstance().saveRequest(
                    DataService.getInstance().getCurrentWorkspace().getId(),
                    collection.getId(),
                    requestDto
            );
        }catch (Throwable e){
            DialogFactory.createDialogException(this, e);
        }

        // @TODO: remove unsaved-hint
    }
}
