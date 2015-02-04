package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
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

    public HttpPost getFetchProblemGradingFilesRequest(String problemJid) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(sandalphonBaseUrl + "/problem/programming/download/grading");

        List<BasicNameValuePair> nameValuePairs = ImmutableList.of(
                new BasicNameValuePair("graderClientJid", sandalphonClientJid),
                new BasicNameValuePair("graderClientSecret", sandalphonClientSecret),
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

            String baseDirName = properties.getProperty("gabriel.baseDir").replaceAll("\"", "");

            File baseDir = new File(baseDirName);
            if (!baseDir.isDirectory()) {
                throw new RuntimeException("gabriel.baseDir: " + baseDirName + " does not exist");
            }

            try {
                INSTANCE.problemDir = new File(baseDir, "problem");
                FileUtils.forceMkdir(INSTANCE.problemDir);

                INSTANCE.tempDir = new File(baseDir, "sandbox");
                FileUtils.forceMkdir(INSTANCE.tempDir);

                INSTANCE.workerDir = new File(baseDir, "temp");
                FileUtils.forceMkdir(INSTANCE.workerDir);

            } catch (IOException e) {
                throw new RuntimeException("Cannot create folder inside " + baseDir.getAbsolutePath());
            }

            INSTANCE.sandalphonBaseUrl = properties.getProperty("sandalphon.baseUrl").replaceAll("\"", "");
            INSTANCE.sandalphonClientJid = properties.getProperty("sandalphon.clientJid");
            INSTANCE.sandalphonClientSecret = properties.getProperty("sandalphon.clientSecret");
        }
        return INSTANCE;
    }

    private static void verifyProperties(Properties properties) {
        List<String> requiredKeys = ImmutableList.of(
                "gabriel.baseDir",
                "sandalphon.baseUrl",
                "sandalphon.clientJid",
                "sandalphon.clientSecret"
        );

        for (String key : requiredKeys) {
            if (properties.get(key) == null) {
                throw new RuntimeException("Missing " + key + " property in conf/application.conf");
            }
        }
    }
}
