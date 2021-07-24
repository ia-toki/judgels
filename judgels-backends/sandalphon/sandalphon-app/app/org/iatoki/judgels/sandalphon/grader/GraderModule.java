package org.iatoki.judgels.sandalphon.grader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Named;
import judgels.sandalphon.SandalphonConfiguration;

public final class GraderModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    @Named("gradingRequestQueueName")
    String gradingRequestQueueName(SandalphonConfiguration config) {
        return config.getGraderConfig().getGradingRequestQueueName();
    }

    @Provides
    @Named("gradingResponseQueueName")
    String gradingResponseQueueName(SandalphonConfiguration config) {
        return config.getGraderConfig().getGradingResponseQueueName();
    }
}
