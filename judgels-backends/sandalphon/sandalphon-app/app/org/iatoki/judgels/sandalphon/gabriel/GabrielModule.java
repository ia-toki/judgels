package org.iatoki.judgels.sandalphon.gabriel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Named;
import judgels.sandalphon.SandalphonConfiguration;

public final class GabrielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    @Named("gradingRequestQueueName")
    String gradingRequestQueueName(SandalphonConfiguration config) {
        return config.getGabrielConfig().getGradingRequestQueueName();
    }

    @Provides
    @Named("gradingResponseQueueName")
    String gradingResponseQueueName(SandalphonConfiguration config) {
        return config.getGabrielConfig().getGradingResponseQueueName();
    }
}
