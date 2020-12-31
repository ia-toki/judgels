package org.iatoki.judgels.sandalphon;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponsePoller;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.JPAApi;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated Temporary class. Will be restructured when new module system has been finalized.
 */
@Singleton
@Deprecated
public final class SandalphonThreadsScheduler {

    @Inject
    public SandalphonThreadsScheduler(ActorSystem actorSystem, JPAApi jpaApi, SandalphonConfiguration config, ProgrammingSubmissionService programmingSubmissionService, @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader, MessageService messageService) {
        Scheduler scheduler = actorSystem.scheduler();
        ExecutionContextExecutor context = actorSystem.dispatcher();

        GradingResponsePoller poller = new GradingResponsePoller(scheduler, context, jpaApi, programmingSubmissionService, sealtielClientAuthHeader, messageService, TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS));

        if (config.getSealtielConfig().isPresent()) {
            scheduler.schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), poller, context);
        }
    }
}
