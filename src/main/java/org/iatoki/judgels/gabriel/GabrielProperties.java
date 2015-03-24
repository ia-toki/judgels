package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

public final class GabrielProperties {
    private static GabrielProperties INSTANCE;

    private File problemDir;
    private File tempDir;
    private File workerDir;

    private String sandalphonBaseUrl;
    private String sandalphonClientJid;
    private String sandalphonClientSecret;

    private String isolatePath;
    private String iwrapperPath;

    private String sealtielBaseUrl;
    private String sealtielClientJid;
    private String sealtielClientSecret;

    private GabrielProperties() {

    }

    public File getProblemDir() {
        return problemDir;
    }

    public File getTempDir() {
        return tempDir;
    }

    public File getWorkerDir() {
        return workerDir;
    }

    public String getIsolatePath() {
        return isolatePath;
    }

    public String getIwrapperPath() {
        return iwrapperPath;
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

    public static GabrielProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GabrielProperties();

            InputStream config = GabrielProperties.class.getClassLoader().getResourceAsStream("conf/application.conf");

            if (config == null) {
                throw new RuntimeException("Missing conf/application.conf file");
            }

            Properties properties = new Properties();

            try {
                properties.load(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            verifyProperties(properties);

            String baseDataDirName = properties.getProperty("gabriel.baseDataDir").replaceAll("\"", "");

            File baseDataDir = new File(baseDataDirName);
            if (!baseDataDir.isDirectory()) {
                throw new RuntimeException("gabriel.baseDataDir: " + baseDataDirName + " does not exist");
            }

            try {
                INSTANCE.problemDir = new File(baseDataDir, "problem");
                FileUtils.forceMkdir(INSTANCE.problemDir);

                INSTANCE.tempDir = new File(baseDataDir, "sandbox");
                FileUtils.forceMkdir(INSTANCE.tempDir);

                INSTANCE.workerDir = new File(baseDataDir, "temp");
                FileUtils.forceMkdir(INSTANCE.workerDir);

            } catch (IOException e) {
                throw new RuntimeException("Cannot create folder inside " + baseDataDir.getAbsolutePath());
            }

            INSTANCE.sandalphonBaseUrl = properties.getProperty("sandalphon.baseUrl").replaceAll("\"", "");
            INSTANCE.sandalphonClientJid = properties.getProperty("sandalphon.clientJid").replaceAll("\"", "");
            INSTANCE.sandalphonClientSecret = properties.getProperty("sandalphon.clientSecret").replaceAll("\"", "");

            INSTANCE.isolatePath = properties.getProperty("moe.isolatePath");
            INSTANCE.iwrapperPath = properties.getProperty("moe.iwrapperPath");

            INSTANCE.sealtielBaseUrl = properties.getProperty("sealtiel.baseUrl").replaceAll("\"", "");
            INSTANCE.sealtielClientJid = properties.getProperty("sealtiel.clientJid").replaceAll("\"", "");
            INSTANCE.sealtielClientSecret = properties.getProperty("sealtiel.clientSecret").replaceAll("\"", "");
        }
        return INSTANCE;
    }

    private static void verifyProperties(Properties properties) {
        List<String> requiredKeys = ImmutableList.of(
                "gabriel.baseDataDir",
                "sandalphon.baseUrl",
                "sandalphon.clientJid",
                "sandalphon.clientSecret",
                "sealtiel.baseUrl",
                "sealtiel.clientJid",
                "sealtiel.clientSecret"
        );

        for (String key : requiredKeys) {
            if (properties.get(key) == null) {
                throw new RuntimeException("Missing " + key + " property in conf/application.conf");
            }
        }
    }
}
