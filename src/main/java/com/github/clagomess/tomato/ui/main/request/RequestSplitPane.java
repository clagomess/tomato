package com.github.clagomess.tomato.ui.main.request;

import com.github.clagomess.tomato.controller.main.request.RequestSplitPaneController;
import com.github.clagomess.tomato.dto.data.RequestDto;
import com.github.clagomess.tomato.dto.key.TabKey;
import com.github.clagomess.tomato.dto.tree.RequestHeadDto;
import com.github.clagomess.tomato.ui.component.ColorConstant;
import com.github.clagomess.tomato.ui.component.ExceptionDialog;
import com.github.clagomess.tomato.ui.component.IconButton;
import com.github.clagomess.tomato.ui.component.WaitExecution;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import com.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.*;
import com.github.clagomess.tomato.ui.main.request.codesnippet.CodeSnippetFrame;
import com.github.clagomess.tomato.ui.main.request.left.*;
import com.github.clagomess.tomato.ui.main.request.right.ResponseTabContent;
import com.github.clagomess.tomato.ui.request.RequestRenameFrame;
import com.github.clagomess.tomato.ui.request.RequestSaveFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static com.github.clagomess.tomato.ui.component.PreventDefaultFrame.disposeIfExists;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

@Slf4j
@Getter
public class RequestSplitPane extends JPanel {
    private static final Icon BLOCK_ICON = new BxBlockIcon(Color.RED);
    private static final Icon GLOBE_ICON = new BxGlobeIcon();
    private static final Icon EDIT_ICON = new BxEditIcon();
    private static final Icon CODE_ALT_ICON = new BxCodeAltIcon();
    private static final Icon SAVE_ICON = new BxSaveIcon();
    private static final Icon SEND_ICON = new BxSendIcon();

    private final TabKey key;
    private RequestHeadDto requestHeadDto;
    private final RequestDto requestDto;

    private final RequestNameTextField txtRequestName = new RequestNameTextField();
    private final IconButton btnViewRenderedUrl = new IconButton(
            GLOBE_ICON,
            "View Rendered Url"
    );
    private final IconButton btnRename = new IconButton(
            EDIT_ICON,
            "Rename"
    );
    private final IconButton btnCodeSnippet = new IconButton(
            CODE_ALT_ICON,
        "Code Snippet"
    );
    private final IconButton btnSaveRequest = new IconButton(
            SAVE_ICON,
        "Save"
    );

    private final HttpMethodComboBox cbHttpMethod = new HttpMethodComboBox();
    private final EnvTextField txtRequestUrl = new EnvTextField(EnvTextfieldOptions.builder().build());
    private final JButton btnSendRequest = new JButton("Send", SEND_ICON);
    private final JButton btnCancelRequest = new JButton(BLOCK_ICON){{
        setToolTipText("Cancel");
        setEnabled(false);
    }};

    private final RequestTabContent requestContent;
    private final ResponseTabContent responseContent;

    private final RequestStagingMonitor requestStagingMonitor;
    private final RequestSplitPaneController controller = new RequestSplitPaneController();


    public RequestSplitPane(
            @NotNull TabKey key,
            @NotNull RequestStagingMonitor requestStagingMonitor,
            @Nullable RequestHeadDto requestHeadDto,
            @NotNull RequestDto requestDto
    ) {
        if(!isEventDispatchThread()) throw new IllegalThreadStateException();

        this.key = key;
        this.requestStagingMonitor = requestStagingMonitor;
        this.requestHeadDto = requestHeadDto;
        this.requestDto = requestDto;

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
        paneRequestName.add(btnRename);
        paneRequestName.add(btnCodeSnippet);
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
        btnRename.addActionListener(l -> btnRenameAction());
        btnCodeSnippet.addActionListener(l -> btnCodeSnippetAction());
        btnSaveRequest.addActionListener(l -> btnSaveRequestAction());
        addSaveKeyboardAction();

        // # REQUEST / RESPONSE
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new MatteBorder(1, 0, 0, 0, ColorConstant.GRAY));
        splitPane.setDividerLocation(580);
        splitPane.setResizeWeight(0.3);
        splitPane.setContinuousLayout(true);

        this.requestContent = new RequestTabContent(
                this.key,
                requestDto,
                requestStagingMonitor
        );

        this.responseContent = new ResponseTabContent(
                this.key
        );

        splitPane.setLeftComponent(requestContent);
        splitPane.setRightComponent(responseContent);
        add(splitPane, "height 100%");
    }

    protected boolean canPerformRequest(){
        try{
            if(!controller.isProductionEnvironment()) return true;

            int ret = JOptionPane.showConfirmDialog(
                    this,
                    "You are using the PRODUCTION environment!\nDo you really want to continue?",
                    "Production Protection",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            return ret == JOptionPane.OK_OPTION;
        } catch (Exception e) {
            new ExceptionDialog(this, e);
        }

        return false;
    }

    public void btnSendRequestAction(){
        if(!canPerformRequest()) return;

        btnSendRequest.setEnabled(false);
        btnCancelRequest.setEnabled(true);
        responseContent.reset();

        Arrays.stream(btnCancelRequest.getActionListeners())
                .forEach(btnCancelRequest::removeActionListener);

        Thread requestThread = controller.sendRequest(
                requestDto,
                response -> invokeLater(() -> {
                    if(response != null) responseContent.update(response);
                    btnSendRequest.setEnabled(true);
                    btnCancelRequest.setEnabled(false);
                }),
                throwable -> invokeLater(() ->
                    new ExceptionDialog(this, throwable)
                )
        );

        btnCancelRequest.addActionListener(l -> {
            btnCancelRequest.setEnabled(false);
            requestThread.interrupt();
        });
    }

    public void btnViewRenderedUrlAction(){
        disposeIfExists(
                ViewRenderedUrlFrame.class,
                () -> new WaitExecution(
                        btnViewRenderedUrl,
                        () -> new ViewRenderedUrlFrame(this, requestDto)
                ).execute()
        );
    }

    public void btnCodeSnippetAction(){
        disposeIfExists(
                CodeSnippetFrame.class,
                () -> new WaitExecution(
                        btnCodeSnippet,
                        () -> new CodeSnippetFrame(this, requestDto)
                ).execute()
        );
    }

    protected void btnRenameAction(){
        disposeIfExists(
                RequestRenameFrame.class,
                () -> new RequestRenameFrame(this, requestHeadDto)
        );
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
            controller.save(requestHeadDto, requestDto);
            requestStagingMonitor.reset();
        }).execute();
    }

    public void addSaveKeyboardAction() {
        this.registerKeyboardAction(
                l -> btnSaveRequestAction(),
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void dispose(){
        txtRequestName.dispose();
        txtRequestUrl.dispose();
        requestContent.dispose();
    }
}
