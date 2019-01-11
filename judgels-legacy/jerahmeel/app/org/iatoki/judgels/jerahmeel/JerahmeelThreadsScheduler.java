package org.iatoki.judgels.jerahmeel;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.jerahmeel.statistic.StatisticUpdater;
import org.iatoki.judgels.jerahmeel.statistic.point.PointStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problemscore.ProblemScoreStatisticService;
import org.iatoki.judgels.jerahmeel.statistic.problem.ProblemStatisticService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponsePoller;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.db.jpa.JPAApi;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated Temporary class. Will be restructured when new module system has been finalized.
 */
@Singleton
@Deprecated
public final class JerahmeelThreadsScheduler {

    @Inject
    public JerahmeelThreadsScheduler(ActorSystem actorSystem, JPAApi jpaApi, ProgrammingSubmissionService programmingSubmissionService, SealtielClientAPI sealtielClientAPI, JophielClientAPI jophielClientAPI, BundleSubmissionService bundleSubmissionService, PointStatisticService pointStatisticService, ProblemScoreStatisticService problemScoreStatisticService, ProblemStatisticService problemStatisticService) {
        Scheduler scheduler = actorSystem.scheduler();
        ExecutionContextExecutor context = actorSystem.dispatcher();

        GradingResponsePoller poller = new GradingResponsePoller(scheduler, context, programmingSubmissionService, sealtielClientAPI, TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS));
        StatisticUpdater pointStatisticUpdater = new StatisticUpdater(jpaApi, bundleSubmissionService, pointStatisticService, problemScoreStatisticService, problemStatisticService, programmingSubmissionService);

        scheduler.schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), poller, context);

        // Disabled until we found better implementation
        // scheduler.schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(1, TimeUnit.HOURS), pointStatisticUpdater, context);
    }
}
