package org.iatoki.judgels.sandalphon;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.SandalphonConfiguration;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponsePoller;
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

        if (config.getSealtielConfig().isPresent()) {
            scheduler.schedule(
                    Duration.create(1, TimeUnit.SECONDS),
                    Duration.create(3, TimeUnit.SECONDS),
                    gradingResponsePoller,
                    context);
        }
    }
}
