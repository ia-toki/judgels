package org.iatoki.judgels.play;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import java.util.Map;

/**
 * @deprecated Use individual config instead.
 */
@Deprecated
public final class JudgelsPlayProperties {

    private static JudgelsPlayProperties INSTANCE;

    private final String appName;
    private final String appVersion;

    private final Config config;

    private String appTitle;
    private String appCopyright;
    private String canonicalUrl;
    private String githubLink;
    private String metaKeywords;
    private String metaDescription;
    private boolean usingGoogleAnalytics;
    private String googleAnalyticsId;
    private String googleAnalyticsViewId;
    private boolean usingGoogleServiceAccount;
    private String googleServiceAccountClientId;
    private String googleServiceAccountClientEmail;
    private String googleServiceAccountPrivateKeyId;
    private String googleServiceAccountPrivateKey;
    private boolean usingBanner;
    private String bannerLink;
    private String bannerImageSrc;

    private Map<String, String> sponsors;

    private JudgelsPlayProperties(String appName, String appVersion, Config config) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.config = config;
    }

    public static synchronized void buildInstance(String appName, String appVersion, Config config) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("JudgelsPlayProperties instance has already been built");
        }
        INSTANCE = new JudgelsPlayProperties(appName, appVersion, config);
        INSTANCE.build();
    }

    public static JudgelsPlayProperties getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("JudgelsPlayProperties instance has not been built");
        }
        return INSTANCE;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppCopyright() {
        return appCopyright;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public String getGithubLink() {
        return githubLink;
    }

    public boolean isUsingGoogleAnalytics() {
        return usingGoogleAnalytics;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public String getGoogleAnalyticsViewId() {
        return googleAnalyticsViewId;
    }

    public boolean isUsingGoogleServiceAccount() {
        return usingGoogleServiceAccount;
    }

    public String getGoogleServiceAccountClientId() {
        return googleServiceAccountClientId;
    }

    public String getGoogleServiceAccountClientEmail() {
        return googleServiceAccountClientEmail;
    }

    public String getGoogleServiceAccountPrivateKeyId() {
        return googleServiceAccountPrivateKeyId;
    }

    public String getGoogleServiceAccountPrivateKey() {
        return googleServiceAccountPrivateKey;
    }

    public boolean isUsingBanner() {
        return usingBanner;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public String getBannerImageSrc() {
        return bannerImageSrc;
    }

    public Map<String, String> getSponsors() {
        return sponsors;
    }

    private void build() {
        this.appTitle = requireStringValue("general.title");
        this.appCopyright = requireStringValue("general.copyright");
        this.canonicalUrl = requireStringValue("general.canonicalUrl");
        this.metaKeywords = requireStringValue("seo.metaKeywords");
        this.metaDescription = requireStringValue("seo.metaDescription");
        this.githubLink = requireStringValue("general.githubUrl");
        this.usingGoogleAnalytics = requireBooleanValue("google.analytics.use");
        this.googleAnalyticsId = getStringValue("google.analytics.id");
        this.googleAnalyticsViewId = getStringValue("google.analytics.viewId");
        this.usingGoogleServiceAccount = requireBooleanValue("google.serviceAccount.use");
        this.googleServiceAccountClientId = getStringValue("google.serviceAccount.clientId");
        this.googleServiceAccountClientEmail = getStringValue("google.serviceAccount.clientEmail");
        this.googleServiceAccountPrivateKeyId = getStringValue("google.serviceAccount.privateKeyId");
        this.googleServiceAccountPrivateKey = getStringValue("google.serviceAccount.privateKey");
        this.usingBanner = requireBooleanValue("banner.use");
        this.bannerLink = getStringValue("banner.link");
        this.bannerImageSrc = getStringValue("banner.imageSrc");
        this.sponsors = getMap("sponsors", String.class);
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

    private <T> Map<String, T> requireMap(String key, Class<T> clazz) {
        ImmutableMap.Builder<String, T> mapBuilder = ImmutableMap.builder();
        com.typesafe.config.Config tempConfig = config.getConfig(key);
        for (Map.Entry<String, ConfigValue> entrySet : tempConfig.entrySet()) {
            mapBuilder.put(entrySet.getKey(), clazz.cast(entrySet.getValue().unwrapped()));
        }

        return mapBuilder.build();
    }

    private <T> Map<String, T> getMap(String key, Class<T> clazz) {
        if (!config.hasPath(key)) {
            return null;
        }

        return requireMap(key, clazz);
    }
}
