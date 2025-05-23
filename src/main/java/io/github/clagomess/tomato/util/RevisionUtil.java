package io.github.clagomess.tomato.util;

import java.util.Properties;

public class RevisionUtil {
    public static final String DEPLOY_DATE;
    public static final String DEPLOY_COMMIT;
    public static final String DEPLOY_TAG;
    public static final String REVISION;

    private RevisionUtil() {}

    static {
        try {
            Properties properties = new Properties();
            properties.load(
                    RevisionUtil.class.getClassLoader()
                            .getResourceAsStream("git.properties")
            );

            DEPLOY_DATE = properties.getProperty("git.build.time");
            DEPLOY_COMMIT = properties.getProperty("git.commit.id.abbrev");
            DEPLOY_TAG = properties.getProperty("git.closest.tag.name")
                    .replace("v", "");

            REVISION = String.format(
                    "%s - %s - %s",
                    DEPLOY_TAG,
                    DEPLOY_COMMIT,
                    DEPLOY_DATE
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
