package org.iatoki.judgels.sandalphon;

import com.typesafe.config.Config;
import java.util.HashSet;
import java.util.Set;
import judgels.gabriel.api.GabrielClientConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.service.api.client.Client;

public final class SandalphonProperties {

    private static final SandalphonProperties INSTANCE = new SandalphonProperties();

    private SandalphonProperties() {}

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
                        .build())
                .gabrielConfig(new GabrielClientConfiguration.Builder()
                        .gradingRequestQueueName(config.getString("gabriel.gradingRequestQueueName"))
                        .gradingResponseQueueName(config.getString("gabriel.gradingResponseQueueName"))
                        .build());

        return sandalphonConfig.build();
    }

    public static SandalphonProperties getInstance() {
        return INSTANCE;
    }
}
