package io.github.clagomess.tomato.ui.main.request.left;

import io.github.clagomess.tomato.dto.data.TomatoID;
import io.github.clagomess.tomato.dto.data.WorkspaceDto;
import io.github.clagomess.tomato.dto.data.workspace.ProxyDto;
import io.github.clagomess.tomato.io.repository.WorkspaceRepository;
import io.github.clagomess.tomato.ui.component.IconButton;
import io.github.clagomess.tomato.ui.component.WaitExecution;
import io.github.clagomess.tomato.ui.component.svgicon.boxicons.BxEditIcon;
import io.github.clagomess.tomato.ui.workspace.edit.WorkspaceEditFrame;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.clagomess.tomato.ui.component.PreventDefaultFrame.toFrontIfExists;

public class ProxyComboBox extends JPanel {
    private static final Icon EDIT_ICON = new BxEditIcon();

    @Getter
    private final ComboBox comboBox = new ComboBox();

    private final JButton btnEdit = new IconButton(
            EDIT_ICON,
            "Edit Proxies"
    );

    public ProxyComboBox(){
        setLayout(new MigLayout(
                "insets 2",
                "[grow, fill][]"
        ));
        add(comboBox, "width ::100% - 32px");
        add(btnEdit);

        // setup
        btnEdit.addActionListener(e -> btnEditAction());
    }

    public void btnEditAction() {
        var workspace = new AtomicReference<WorkspaceDto>();

        new WaitExecution(this, btnEdit)
                .thenBackground(() -> workspace.set(new WorkspaceRepository().getDataSessionWorkspace()))
                .thenEdt(() -> toFrontIfExists(
                        WorkspaceEditFrame.class,
                        () -> new WorkspaceEditFrame(this, workspace.get())
                ))
                .execute();
    }

    static class ComboBox extends JComboBox<ProxyDto> {
        public void refresh(@Nullable TomatoID proxyId){
            var proxies = new AtomicReference<List<ProxyDto>>();

            new WaitExecution(this).thenBackground(() -> proxies.set(
                    new WorkspaceRepository()
                            .getDataSessionWorkspace()
                            .getProxies()
            )).thenEdt(() -> {
                removeAllItems();
                addItem(null);
                super.setSelectedItem(null);

                for (var item : proxies.get()){
                    addItem(item);
                    if (Objects.equals(item.getId(), proxyId)){
                        super.setSelectedItem(item);
                    }
                }
            }).execute();
        }

        @Override
        public ProxyDto getSelectedItem() {
            return (ProxyDto) super.getSelectedItem();
        }

        public void setSelectedItem(@Nullable TomatoID proxyId) {
            refresh(proxyId);
        }
    }
}
