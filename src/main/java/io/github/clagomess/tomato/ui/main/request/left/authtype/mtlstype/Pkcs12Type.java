package io.github.clagomess.tomato.ui.main.request.left.authtype.mtlstype;

import io.github.clagomess.tomato.dto.data.request.auth.MTLSAuthDto.Pkcs12Dto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.RequestFileChooser;
import io.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import io.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;

public class Pkcs12Type extends JPanel implements DisposableListener {
    private final EnvTextField password;
    private final EnvTextField alias;

    public Pkcs12Type(
            @Nullable RequestHeadDto requestHead,
            Pkcs12Dto pkcs12,
            RequestStagingMonitor requestStagingMonitor
    ){
        setLayout(new MigLayout(
                "insets 5 2 2 2",
                "[grow, fill]"
        ));

        var file = new RequestFileChooser(requestHead);
        file.setValue(pkcs12.getFile());
        file.addOnChange(value -> {
            pkcs12.setFile(value);
            requestStagingMonitor.update();
        });

        password = new EnvTextField(requestHead, EnvTextfieldOptions.builder().build());
        password.setText(pkcs12.getPassword());
        password.addOnChange(value -> {
            pkcs12.setPassword(value);
            requestStagingMonitor.update();
        });

        alias = new EnvTextField(requestHead, EnvTextfieldOptions.builder().build());
        alias.setText(pkcs12.getAlias());
        alias.addOnChange(value -> {
            pkcs12.setAlias(value);
            requestStagingMonitor.update();
        });

        add(new JLabel("File:"), "wrap");
        add(file, "wrap");
        add(new JLabel("Password:"), "wrap");
        add(password, "wrap");
        add(new JLabel("Alias (optional):"), "wrap");
        add(alias, "wrap");
    }

    @Override
    public void dispose() {
        password.dispose();
        alias.dispose();
    }
}
