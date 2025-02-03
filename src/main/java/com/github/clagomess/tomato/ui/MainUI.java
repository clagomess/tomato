package com.github.clagomess.tomato.ui;

import com.github.clagomess.tomato.ui.collection.CollectionImportUI;
import com.github.clagomess.tomato.ui.collection.CollectionNewUI;
import com.github.clagomess.tomato.ui.component.favicon.FaviconImage;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxExportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxImportIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxSliderAltIcon;
import com.github.clagomess.tomato.ui.component.svgicon.boxicons.BxTransferAltIcon;
import com.github.clagomess.tomato.ui.environment.EnvironmentImportUI;
import com.github.clagomess.tomato.ui.environment.EnvironmentNewUI;
import com.github.clagomess.tomato.ui.environment.list.EnvironmentListUI;
import com.github.clagomess.tomato.ui.main.collection.CollectionTreeUI;
import com.github.clagomess.tomato.ui.main.request.RequestTabPaneUI;
import com.github.clagomess.tomato.ui.settings.AboutUI;
import com.github.clagomess.tomato.ui.settings.ConfigurationUI;
import com.github.clagomess.tomato.ui.settings.DebugPublisherUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceNewUI;
import com.github.clagomess.tomato.ui.workspace.WorkspaceSwitchUI;
import com.github.clagomess.tomato.ui.workspace.list.WorkspaceListUI;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    public MainUI(){
        setTitle("Tomato");
        setIconImages(FaviconImage.getFrameIconImage());
        setVisible(true);
        setMinimumSize(new Dimension(1200, 650));
        setJMenuBar(getMenu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new CollectionTreeUI(),
                new RequestTabPaneUI()
        );
        splitPane.setDividerLocation(250);

        add(splitPane);
        pack();
        setVisible(true);
    }

    public JMenuBar getMenu(){
        var mainUI = this;
        JMenuBar menuBar = new JMenuBar();

        // workspace
        menuBar.add(new JMenu("Workspace"){{
            add(new JMenuItem("Switch", new BxTransferAltIcon()){{
                addActionListener(l -> new WorkspaceSwitchUI(mainUI));
            }});
            add(new JMenuItem("New Workspace"){{
                addActionListener(l -> new WorkspaceNewUI(mainUI));
            }});
            add(new JMenuItem("Edit Workspaces"){{
                addActionListener(l -> new WorkspaceListUI(mainUI));
            }});
        }});

        // collection
        menuBar.add(new JMenu("Collection"){{
            add(new JMenuItem("New Collection"){{
                addActionListener(l -> new CollectionNewUI(mainUI, null));
            }});
            add(new JMenuItem("Import", new BxImportIcon()){{
                addActionListener(l -> new CollectionImportUI(mainUI, null));
            }});
            add(new JMenuItem("Export", new BxExportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements - Export Collection
            }});
        }});

        // environment
        menuBar.add(new JMenu("Environment"){{
            add(new JMenuItem("New Environment"){{
                addActionListener(l -> new EnvironmentNewUI(mainUI));
            }});
            add(new JMenuItem("Edit Environments"){{
                addActionListener(l -> new EnvironmentListUI(mainUI));
            }});
            add(new JMenuItem("Import", new BxImportIcon()){{
                addActionListener(l -> new EnvironmentImportUI(mainUI));
            }});
            add(new JMenuItem("Export", new BxExportIcon()){{
                addActionListener(l -> {}); //@TODO: Implements - Export Environment
            }});
        }});

        // about
        menuBar.add(new JMenu("Settings"){{
            add(new JMenuItem("Configuration", new BxSliderAltIcon()){{
                addActionListener(l -> new ConfigurationUI());
            }});
            add(new JMenuItem("About"){{
                addActionListener(l -> new AboutUI());
            }});
            add(new JMenuItem("Debug -> Publisher"){{
                addActionListener(l -> new DebugPublisherUI());
            }});
        }});

        return menuBar;
    }
}
