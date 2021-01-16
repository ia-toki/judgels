package org.iatoki.judgels.sandalphon;

import com.typesafe.config.Config;
import java.util.HashSet;
import java.util.Set;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.service.api.client.Client;

public final class SandalphonProperties {

    private static final SandalphonProperties INSTANCE = new SandalphonProperties();

    public static SandalphonConfiguration build(Config config) {
        Set<Client> clients = new HashSet<>();
        for (String client : config.getStringList("sandalphon.clients")) {
            String[] tokens = client.split(":");
            if (tokens.length == 2) {
                clients.add(Client.of(tokens[0], tokens[1]));
            }
        }

        SandalphonConfiguration.Builder sandalphonConfig = new SandalphonConfiguration.Builder()
                .baseDataDir(config.getString("sandalphon.baseDataDir"))
                .clients(clients)
                .jophielConfig(new JophielClientConfiguration.Builder()
                        .baseUrl(config.getString("jophiel.baseUrl"))
                        .build());

        if (config.hasPath("sealtiel.baseUrl")) {
            sandalphonConfig.sealtielConfig(new SealtielClientConfiguration.Builder()
                    .baseUrl(config.getString("sealtiel.baseUrl"))
                    .clientJid(config.getString("sealtiel.clientJid"))
                    .clientSecret(config.getString("sealtiel.clientSecret"))
                    .build());
        }

        if (config.hasPath("gabriel.clientJid")) {
            sandalphonConfig.gabrielConfig(new GabrielClientConfiguration.Builder()
                    .clientJid(config.getString("gabriel.clientJid"))
                    .build());
        } else {
            sandalphonConfig.gabrielConfig(GabrielClientConfiguration.DEFAULT);
        }

        return sandalphonConfig.build();
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
