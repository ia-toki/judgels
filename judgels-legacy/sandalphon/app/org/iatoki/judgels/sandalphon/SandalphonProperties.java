package org.iatoki.judgels.sandalphon;

import com.typesafe.config.Config;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;

public final class SandalphonProperties {

    private static final SandalphonProperties INSTANCE = new SandalphonProperties();

    public static SandalphonConfiguration build(Config config) {
        return new SandalphonConfiguration.Builder()
                .baseDataDir(config.getString("sandalphon.baseDataDir"))
                .jophielConfig(new JophielClientConfiguration.Builder()
                        .baseUrl(config.getString("jophiel.baseUrl"))
                        .build())
                .sealtielConfig(new SealtielClientConfiguration.Builder()
                        .baseUrl(config.getString("sealtiel.baseUrl"))
                        .clientJid(config.getString("sealtiel.clientJid"))
                        .clientSecret(config.getString("sealtiel.clientSecret"))
                        .build())
                .gabrielConfig(new GabrielClientConfiguration.Builder()
                        .clientJid(config.getString("gabriel.clientJid"))
                        .build())
                .raphaelBaseUrl(config.getString("raphael.baseUrl"))
                .build();
    }

    public static SandalphonProperties getInstance() {
        return INSTANCE;
    }

    // TODO: put these into separate modules

    public String getBaseProblemsDirKey() {
        return "problems";
    }

    public String getBaseProblemClonesDirKey() {
        return "problem-clones";
    }

    public String getBaseLessonsDirKey() {
        return "lessons";
    }

    public String getBaseLessonClonesDirKey() {
        return "lesson-clones";
    }
}
