package io.github.clagomess.tomato.ui.main.request.left.authtype;

import io.github.clagomess.tomato.dto.data.request.auth.MTLSAuthDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.ColorConstant;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import io.github.clagomess.tomato.ui.main.request.left.authtype.mtlstype.Pkcs12Type;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;

public class MTLSAuthType extends JPanel implements DisposableListener {
    private final RequestHeadDto requestHead;
    private final MTLSAuthDto mtlsAuth;
    private final RequestStagingMonitor requestStagingMonitor;
    private final ButtonGroup bgMtlsType = new ButtonGroup();
    private Component currentMtlsType;

    public MTLSAuthType(
            @Nullable RequestHeadDto requestHead,
            MTLSAuthDto mtlsAuth,
            RequestStagingMonitor requestStagingMonitor
    ){
        this.requestHead = requestHead;
        this.mtlsAuth = mtlsAuth;
        this.requestStagingMonitor = requestStagingMonitor;

        setLayout(new MigLayout(
                "insets 0 0 0 0",
                "[grow, fill]"
        ));

        JPanel pRadioMtlsType = new JPanel(new MigLayout(
                "insets 5 2 5 2, alignx center",
                "[]"
        ));
        pRadioMtlsType.setBorder(new MatteBorder(0, 0, 1, 0, ColorConstant.GRAY));

        Arrays.stream(MTLSAuthDto.Type.values()).forEach(item -> {
            JRadioButton rbMtlsType = new JRadioButton(item.getDescription());
            rbMtlsType.setSelected(item == mtlsAuth.getType());

            rbMtlsType.addActionListener(l -> {
                if(item == mtlsAuth.getType()) return;

                dispose();
                remove(1);
                mtlsAuth.setType(item);
                requestStagingMonitor.update();

                currentMtlsType = getMtlsType();
                add(currentMtlsType, "height 100%");
                revalidate();
                repaint();
            });

            bgMtlsType.add(rbMtlsType);
            pRadioMtlsType.add(rbMtlsType);
        });

        add(pRadioMtlsType, "wrap 0");
        currentMtlsType = getMtlsType();
        add(currentMtlsType, "height 100%");
    }

    private Component getMtlsType(){
        return switch (mtlsAuth.getType()) {
            case PKCS12 -> new Pkcs12Type(
                    requestHead,
                    mtlsAuth.getPkcs12(),
                    requestStagingMonitor
            );
        };
    }

    @Override
    public void dispose() {
        if(currentMtlsType instanceof DisposableListener mtlsType){
            mtlsType.dispose();
        }
    }
}
