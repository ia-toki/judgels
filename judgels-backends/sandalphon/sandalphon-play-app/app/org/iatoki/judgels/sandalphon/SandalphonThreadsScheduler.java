package org.iatoki.judgels.sandalphon;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.service.jaxrs.JudgelsObjectMappers;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponsePoller;
import play.libs.Json;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

@Singleton
public final class SandalphonThreadsScheduler {
    @Inject
    public SandalphonThreadsScheduler(
            ActorSystem actorSystem,
            SandalphonConfiguration config,
            GradingResponsePoller gradingResponsePoller) {

        Scheduler scheduler = actorSystem.scheduler();
        ExecutionContextExecutor context = actorSystem.dispatcher();

        if (config.getRabbitMQConfig().isPresent()) {
            scheduler.schedule(
                    Duration.create(1, TimeUnit.SECONDS),
                    Duration.create(3, TimeUnit.SECONDS),
                    gradingResponsePoller,
                    context);
        }

        Json.setObjectMapper(objectMapper());
    }

    private ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(
                new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule()));
    }
}
