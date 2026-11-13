package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.request.AuthDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.enums.AuthTypeEnum;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.main.request.left.authtype.BasicAuthType;
import io.github.clagomess.tomato.ui.main.request.left.authtype.NoAuthType;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class AuthPanel extends JPanel implements DisposableListener {
    private final RequestHeadDto requestHead;
    private final AuthDto auth;
    private final RequestStagingMonitor requestStagingMonitor;
    private final JComboBox<AuthTypeEnum> cbAuthType = new JComboBox<>(
            AuthTypeEnum.values()
    );
    private Component currentAuthType;

    public AuthPanel(
            @Nullable RequestHeadDto requestHead,
            AuthDto auth,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.requestHead = requestHead;
        this.auth = auth;
        this.requestStagingMonitor = requestStagingMonitor;

        // layout
        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        JPanel pAuthType = new JPanel(new MigLayout(
                "insets 5 2 5 2",
                "[]"
        ));
        pAuthType.setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));
        pAuthType.add(new JLabel("Auth Type:"));
        pAuthType.add(cbAuthType);

        cbAuthType.setSelectedItem(auth.getType());
        cbAuthType.addActionListener(l -> cbAuthTypeAction());

        add(pAuthType, "wrap 0");
        currentAuthType = getAuthType();
        add(currentAuthType, "height 100%");
    }

    private void cbAuthTypeAction(){
        var type = (AuthTypeEnum) cbAuthType.getSelectedItem();
        if(type == null || type == auth.getType()) return;

        dispose();
        remove(1);
        auth.setType(type);
        requestStagingMonitor.update();

        currentAuthType = getAuthType();
        add(currentAuthType, "height 100%");
        revalidate();
        repaint();
    }

    private Component getAuthType(){
        return switch (auth.getType()) {
            case BASIC -> new BasicAuthType(
                    requestHead,
                    auth.getBasic(),
                    requestStagingMonitor
            );
            case NO_AUTH -> new NoAuthType();
        };
    }

    public void dispose() {
        if(currentAuthType instanceof DisposableListener authType){
            authType.dispose();
        }
    }
}
