package org.iatoki.judgels.sandalphon;

import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class SandalphonProperties {

    private static SandalphonProperties INSTANCE;

    private final Config config;

    private File sandalphonBaseDataDir;
    private File submissionLocalDir;

    private String jophielBaseUrl;
    private String jophielClientJid;
    private String jophielClientSecret;

    private String sealtielBaseUrl;
    private String sealtielClientJid;
    private String sealtielClientSecret;
    private String sealtielGabrielClientJid;

    private String raphaelBaseUrl;

    private SandalphonProperties(Config config) {
        this.config = config;
    }

    public static synchronized void buildInstance(Config config) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("SandalphonProperties instance has already been built");
        }

        INSTANCE = new SandalphonProperties(config);
        INSTANCE.build();
    }

    public static SandalphonProperties getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("SandalphonProperties instance has not been built");
        }
        return INSTANCE;
    }

    public String getJophielBaseUrl() {
        return jophielBaseUrl;
    }

    public String getJophielClientJid() {
        return jophielClientJid;
    }

    public String getJophielClientSecret() {
        return jophielClientSecret;
    }

    public String getSealtielBaseUrl() {
        return sealtielBaseUrl;
    }

    public String getSealtielClientJid() {
        return sealtielClientJid;
    }

    public String getSealtielClientSecret() {
        return sealtielClientSecret;
    }

    public String getSealtielGabrielClientJid() {
        return sealtielGabrielClientJid;
    }

    public String getRaphaelBaseUrl() {
        return raphaelBaseUrl;
    }

    public File getSubmissionLocalDir() {
        return submissionLocalDir;
    }

    public File getProblemLocalDir() {
        return sandalphonBaseDataDir;
    }

    public String getBaseProblemsDirKey() {
        return "problems";
    }

    public String getBaseProblemClonesDirKey() {
        return "problem-clones";
    }

    public File getLessonLocalDir() {
        return sandalphonBaseDataDir;
    }

    public String getBaseLessonsDirKey() {
        return "lessons";
    }

    public String getBaseLessonClonesDirKey() {
        return "lesson-clones";
    }

    private void build() {
        sandalphonBaseDataDir = requireDirectoryValue("sandalphon.baseDataDir");

        try {
            submissionLocalDir = new File(sandalphonBaseDataDir, "submissions");
            FileUtils.forceMkdir(submissionLocalDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jophielBaseUrl = requireStringValue("jophiel.baseUrl");
        jophielClientJid = requireStringValue("jophiel.clientJid");
        jophielClientSecret = requireStringValue("jophiel.clientSecret");

        sealtielBaseUrl = requireStringValue("sealtiel.baseUrl");
        sealtielClientJid = requireStringValue("sealtiel.clientJid");
        sealtielClientSecret = requireStringValue("sealtiel.clientSecret");
        sealtielGabrielClientJid = requireStringValue("sealtiel.gabrielClientJid");

        raphaelBaseUrl = requireStringValue("raphael.baseUrl");
    }

    private String getStringValue(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getString(key);
    }

    private String requireStringValue(String key) {
        return config.getString(key);
    }

    private File requireDirectoryValue(String key) {
        String filename = config.getString(key);

        File dir = new File(filename);
        if (!dir.isDirectory()) {
            throw new RuntimeException("Directory " + dir.getAbsolutePath() + " does not exist");
        }
        return dir;
    }
}
