package io.github.clagomess.tomato.ui.component.stagingmonitor;

import static javax.swing.SwingUtilities.invokeLater;

public interface FrameTitleStagingMonitor<T> {
    String PREFIX = "[*] ";
    StagingMonitor<T> getStagingMonitor();

    void setTitle(String title);
    String getTitle();

    default void buildTitle(boolean isChanging){
        String title = getTitle();

        if(isChanging && !title.contains(PREFIX)){
            setTitle(PREFIX + title);
        }

        if(!isChanging){
            setTitle(title.replace(PREFIX, ""));
        }
    }

    default void updateStagingMonitor(){
        getStagingMonitor().update();
        invokeLater(() -> buildTitle(getStagingMonitor().isDiferent()));
    }

    default void resetStagingMonitor(){
        getStagingMonitor().reset();
        invokeLater(() -> buildTitle(false));
    }
}
