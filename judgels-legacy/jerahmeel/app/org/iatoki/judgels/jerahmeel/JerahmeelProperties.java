package org.iatoki.judgels.jerahmeel;

import com.amazonaws.services.s3.model.Region;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class JerahmeelProperties {

    private static JerahmeelProperties INSTANCE;

    private final Config config;

    private String jerahmeelBaseUrl;
    private File jerahmeelBaseDataDir;

    private String jerahmeelWelcomeMessage;

    private String jophielBaseUrl;
    private String jophielClientJid;
    private String jophielClientSecret;

    private String sandalphonBaseUrl;
    private String sandalphonClientJid;
    private String sandalphonClientSecret;

    private String sealtielBaseUrl;
    private String sealtielClientJid;
    private String sealtielClientSecret;
    private String sealtielGabrielClientJid;

    private String raphaelBaseUrl;

    private Boolean globalAWSUsingKeys;
    private String globalAWSAccessKey;
    private String globalAWSSecretKey;
    private Region globalAWSS3Region;

    private boolean submissionUsingAWSS3;
    private Boolean submissionAWSUsingKeys;
    private File submissionLocalDir;
    private String submissionAWSAccessKey;
    private String submissionAWSSecretKey;
    private String submissionAWSS3BucketName;
    private Region submissionAWSS3BucketRegion;

    private String progressApiToken;
    private Map<String, String> progressApiUsers;

    private JerahmeelProperties(Config config) {
        this.config = config;
    }

    public static synchronized void buildInstance(Config config) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("JerahmeelProperties instance has already been built");
        }

        INSTANCE = new JerahmeelProperties(config);
        INSTANCE.build();
    }

    public static JerahmeelProperties getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("JerahmeelProperties instance has not been built");
        }
        return INSTANCE;
    }

    public String getJerahmeelBaseUrl() {
        return jerahmeelBaseUrl;
    }

    public String getJerahmeelWelcomeMessage() {
        return jerahmeelWelcomeMessage;
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

    public String getSealtielGabrielClientJid() {
        return sealtielGabrielClientJid;
    }

    public String getRaphaelBaseUrl() {
        return raphaelBaseUrl;
    }

    public boolean isSubmissionUsingAWSS3() {
        return submissionUsingAWSS3;
    }

    public File getSubmissionLocalDir() {
        return submissionLocalDir;
    }

    public String getProgressApiToken() {
        return progressApiToken;
    }

    public Map<String, String> getProgressApiUsers() {
        return progressApiUsers;
    }

    public String getSubmissionAWSAccessKey() {
        if (!isSubmissionUsingAWSS3()) {
            throw new UnsupportedOperationException("Submission is not using AWS S3");
        }
        if (submissionAWSAccessKey != null) {
            return submissionAWSAccessKey;
        }
        if (globalAWSAccessKey != null) {
            return globalAWSAccessKey;
        }

        throw new RuntimeException("Missing aws.global.key.access or aws.submission.key.access");
    }

    public String getSubmissionAWSSecretKey() {
        if (!isSubmissionUsingAWSS3()) {
            throw new UnsupportedOperationException("Submission is not using AWS S3");
        }
        if (submissionAWSSecretKey != null) {
            return submissionAWSSecretKey;
        }
        if (globalAWSSecretKey != null) {
            return globalAWSSecretKey;
        }

        throw new RuntimeException("Missing aws.global.key.secret or aws.submission.key.secret in");
    }

    public String getSubmissionAWSS3BucketName() {
        if (!isSubmissionUsingAWSS3()) {
            throw new UnsupportedOperationException("Submission is not using AWS S3");
        }
        return submissionAWSS3BucketName;
    }

    public Region getSubmissionAWSS3BucketRegion() {
        if (!isSubmissionUsingAWSS3()) {
            throw new UnsupportedOperationException("Submission is not using AWS S3");
        }
        if (submissionAWSS3BucketRegion != null) {
            return submissionAWSS3BucketRegion;
        }
        if (globalAWSS3Region != null) {
            return globalAWSS3Region;
        }

        throw new RuntimeException("Missing aws.global.s3.bucket.regionId or aws.submission.s3.bucket.regionId");
    }

    public boolean isSubmissionAWSUsingKeys() {
        if (!isSubmissionUsingAWSS3()) {
            throw new UnsupportedOperationException("Submission is not using AWS S3");
        }

        if (submissionAWSUsingKeys != null) {
            return submissionAWSUsingKeys;
        }
        if (globalAWSUsingKeys != null) {
            return globalAWSUsingKeys;
        }

        throw new RuntimeException("Missing aws.global.key.use or aws.submission.key.use in");
    }

    private void build() {
        jerahmeelBaseUrl = requireStringValue("jerahmeel.baseUrl");
        jerahmeelBaseDataDir = requireDirectoryValue("jerahmeel.baseDataDir");

        jerahmeelWelcomeMessage = requireStringValue("jerahmeel.welcomeMessage");

        jophielBaseUrl = requireStringValue("jophiel.baseUrl");
        jophielClientJid = requireStringValue("jophiel.clientJid");
        jophielClientSecret = requireStringValue("jophiel.clientSecret");

        sandalphonBaseUrl = requireStringValue("sandalphon.baseUrl");
        sandalphonClientJid = requireStringValue("sandalphon.clientJid");
        sandalphonClientSecret = requireStringValue("sandalphon.clientSecret");

        sealtielBaseUrl = requireStringValue("sealtiel.baseUrl");
        sealtielClientJid = requireStringValue("sealtiel.clientJid");
        sealtielClientSecret = requireStringValue("sealtiel.clientSecret");
        sealtielGabrielClientJid = requireStringValue("sealtiel.gabrielClientJid");

        raphaelBaseUrl = requireStringValue("raphael.baseUrl");

        globalAWSUsingKeys = getBooleanValue("aws.global.key.use");
        globalAWSAccessKey = getStringValue("aws.global.key.access");
        globalAWSSecretKey = getStringValue("aws.global.key.secret");
        globalAWSS3Region = Region.fromValue(getStringValue("aws.global.s3.bucket.regionId"));

        submissionUsingAWSS3 = requireBooleanValue("aws.submission.s3.use");
        submissionAWSUsingKeys = getBooleanValue("aws.submission.key.use");
        submissionAWSAccessKey = getStringValue("aws.submission.key.access");
        submissionAWSSecretKey = getStringValue("aws.submission.key.secret");
        submissionAWSS3BucketName = getStringValue("aws.submission.s3.bucket.name");
        submissionAWSS3BucketRegion = Region.fromValue(getStringValue("aws.submission.s3.bucket.regionId"));

        progressApiToken = requireStringValue("progressApi.token");
        progressApiUsers = requireMapValue("progressApi.users");

        try {
            submissionLocalDir = new File(jerahmeelBaseDataDir, "submission");
            FileUtils.forceMkdir(submissionLocalDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private Boolean getBooleanValue(String key) {
        if (!config.hasPath(key)) {
            return null;
        }
        return config.getBoolean(key);
    }

    private boolean requireBooleanValue(String key) {
        return config.getBoolean(key);
    }

    private File requireDirectoryValue(String key) {
        String filename = config.getString(key);

        File dir = new File(filename);
        if (!dir.isDirectory()) {
            throw new RuntimeException("Directory " + dir.getAbsolutePath() + " does not exist");
        }
        return dir;
    }

    private Map<String, String> requireMapValue(String key) {
        Map<String, Object> map = config.getObject(key).unwrapped();
        return Maps.transformValues(map, v -> (String)v);
    }
}
