package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public final class GabrielProperties {

    private static GabrielProperties INSTANCE;

    private Config config;

    private File gabrielBaseDataDir;

    private File gabrielProblemDir;
    private File gabrielWorkerDir;

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

    public HttpPost getFetchProblemGradingFilesRequest(String problemJid) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(sandalphonBaseUrl + "/problem/programming/download/grading");

        List<BasicNameValuePair> nameValuePairs = ImmutableList.of(
                new BasicNameValuePair("graderJid", sandalphonClientJid),
                new BasicNameValuePair("graderSecret", sandalphonClientSecret),
                new BasicNameValuePair("problemJid", problemJid)
        );
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return post;
    }

    public HttpPost getGetGradingLastUpdateTimeRequest(String problemJid) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(sandalphonBaseUrl + "/problem/programming/getGradingLastUpdateTime");

        List<BasicNameValuePair> nameValuePairs = ImmutableList.of(
                new BasicNameValuePair("graderJid", sandalphonClientJid),
                new BasicNameValuePair("graderSecret", sandalphonClientSecret),
                new BasicNameValuePair("problemJid", problemJid)
        );
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return post;
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
