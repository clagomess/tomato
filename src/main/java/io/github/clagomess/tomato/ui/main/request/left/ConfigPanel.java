package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.controller.request.left.ConfigPanelController;
import io.github.clagomess.tomato.dto.data.request.ConfigDto;
import io.github.clagomess.tomato.publisher.DisposableListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ConfigPanel extends JPanel implements DisposableListener {
    private final RequestStagingMonitor requestStagingMonitor;
    private final ProxyComboBox proxyComboBox = new ProxyComboBox();

    private final ConfigPanelController controller = new ConfigPanelController();

    private final ConfigDto config;

    public ConfigPanel(
            ConfigDto config,
            RequestStagingMonitor requestStagingMonitor
    ) {
        this.config = config;
        this.requestStagingMonitor = requestStagingMonitor;

        // layout
        setLayout(new MigLayout(
                "insets 10 0 0 0",
                "[][grow, fill]"
        ));

        add(new JLabel("Proxy:"));
        add(proxyComboBox, "wrap");

        // setup
        proxyComboBox.getComboBox().setSelectedItem(config.getProxyId());
        addOnChangeListeners();
        controller.addOnChangeListener(() -> proxyComboBox.getComboBox().refresh(config.getProxyId()));
    }

    private void addOnChangeListeners() {
        proxyComboBox.getComboBox().addActionListener(l -> {
            var result = proxyComboBox.getComboBox().getSelectedItem();
            config.setProxyId(result != null ? result.getId() : null);
            requestStagingMonitor.update();
        });
    }

    public void dispose() {
        controller.dispose();
    }
}
