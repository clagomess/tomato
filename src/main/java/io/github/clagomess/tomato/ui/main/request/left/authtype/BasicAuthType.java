package io.github.clagomess.tomato.ui.main.request.left.authtype;

import io.github.clagomess.tomato.dto.data.request.BasicAuthDto;
import io.github.clagomess.tomato.dto.tree.RequestHeadDto;
import io.github.clagomess.tomato.publisher.DisposableListener;
import io.github.clagomess.tomato.ui.component.envtextfield.EnvTextField;
import io.github.clagomess.tomato.ui.component.envtextfield.EnvTextfieldOptions;
import io.github.clagomess.tomato.ui.main.request.left.RequestStagingMonitor;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;

public class BasicAuthType extends JPanel implements DisposableListener {
    private final EnvTextField username;
    private final EnvTextField password;

    public BasicAuthType(
            @Nullable RequestHeadDto requestHead,
            BasicAuthDto basicAuth,
            RequestStagingMonitor requestStagingMonitor
    ){
        setLayout(new MigLayout(
                "insets 5 2 2 2",
                "[grow, fill]"
        ));

        username = new EnvTextField(requestHead, EnvTextfieldOptions.builder().build());
        username.setText(basicAuth.getUsername());
        username.addOnChange(value -> {
            basicAuth.setUsername(value);
            requestStagingMonitor.update();
        });

        password = new EnvTextField(requestHead, EnvTextfieldOptions.builder().build());
        password.setText(basicAuth.getPassword());
        password.addOnChange(value -> {
            basicAuth.setPassword(value);
            requestStagingMonitor.update();
        });

        add(new JLabel("Username:"), "wrap");
        add(username, "wrap");
        add(new JLabel("Password:"), "wrap");
        add(password, "wrap");
    }

    @Override
    public void dispose() {
        username.dispose();
        password.dispose();
    }
}
