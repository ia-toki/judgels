package org.iatoki.judgels.sandalphon.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;
import judgels.messaging.MessageClient;
import judgels.messaging.rabbitmq.RabbitMQ;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.sandalphon.SandalphonConfiguration;

public final class MessagingModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    RabbitMQConfiguration rabbitMQConfig(SandalphonConfiguration config) {
        return config.getRabbitMQConfig().orElse(RabbitMQConfiguration.DEFAULT);
    }

    @Provides
    @Singleton
    MessageClient messageClient(RabbitMQConfiguration rabbitMQConfig, ObjectMapper objectMapper) {
        return new MessageClient(new RabbitMQ(rabbitMQConfig), objectMapper);
    }
}
