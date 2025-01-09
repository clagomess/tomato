package com.github.clagomess.tomato.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
@Getter
public class RevisionUtil {
    private String deployDate = "00/00/0000";
    private String deployCommit = "0000000";
    private String deployTag = "0.0.0";

    @Getter
    private static final RevisionUtil instance = new RevisionUtil();

    private RevisionUtil(){
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));

            deployDate = properties.getProperty("git.build.time");
            deployCommit = properties.getProperty("git.commit.id.abbrev");
            deployTag = properties.getProperty("git.closest.tag.name").replace("v", "");
        }catch (Throwable e){
            log.error(log.getName(), e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %s - %s",
                deployTag,
                deployCommit,
                deployDate
        );
    }
}
