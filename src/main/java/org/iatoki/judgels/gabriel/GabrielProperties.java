package org.iatoki.judgels.gabriel;

import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class GabrielProperties {

    private static GabrielProperties INSTANCE;

    private Config config;

    private File gabrielBaseDataDir;

    private File gabrielProblemDir;
    private File gabrielWorkerDir;

    private String sandalphonLocalBaseDataDir;
    private String sandalphonBaseUrl;
    private String sandalphonClientJid;
    private String sandalphonClientSecret;

    private String moeIsolatePath;
    private String moeIwrapperPath;

    private String sealtielBaseUrl;
    private String sealtielClientJid;
    private String sealtielClientSecret;

    private GabrielProperties(Config config) {
        this.config = config;
    }

    public static synchronized void buildInstance(Config config) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("GabrielProperties instance has already been built");
        }

        INSTANCE = new GabrielProperties(config);
        INSTANCE.build();
    }

    public static GabrielProperties getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("GabrielProperties instance has not been built");
        }
        return INSTANCE;
    }

    public File getProblemDir() {
        return gabrielProblemDir;
    }

    public File getTempDir() {
        return gabrielWorkerDir;
    }

    public String getMoeIsolatePath() {
        return moeIsolatePath;
    }

    public String getMoeIwrapperPath() {
        return moeIwrapperPath;
    }

    public String getSandalphonLocalBaseDataDir() {
        return sandalphonLocalBaseDataDir;
    }

    public String getSandalphonBaseUrl() {
        return sandalphonBaseUrl;
    }

    public String getSandalphonClientJid() {
        return sandalphonClientJid;
    }

    public String getSandalphonClientSecret() {
        return sandalphonClientSecret;
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

    private void build() {
        gabrielBaseDataDir = requireDirectoryValue("gabriel.baseDataDir");

        try {
            gabrielProblemDir = new File(gabrielBaseDataDir, "problems");
            FileUtils.forceMkdir(gabrielProblemDir);

            gabrielWorkerDir = new File(gabrielBaseDataDir, "temp");
            FileUtils.forceMkdir(gabrielWorkerDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        sandalphonLocalBaseDataDir = getStringValue("sandalphon.localBaseDataDir");

        sandalphonBaseUrl = requireStringValue("sandalphon.baseUrl");
        sandalphonClientJid = requireStringValue("sandalphon.clientJid");
        sandalphonClientSecret = requireStringValue("sandalphon.clientSecret");

        sealtielBaseUrl = requireStringValue("sealtiel.baseUrl");
        sealtielClientJid = requireStringValue("sealtiel.clientJid");
        sealtielClientSecret = requireStringValue("sealtiel.clientSecret");

        moeIsolatePath = getStringValue("moe.isolatePath");
        moeIwrapperPath = getStringValue("moe.iwrapperPath");
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
