package org.iatoki.judgels.sandalphon;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.nio.file.Paths;
import java.time.Clock;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.persistence.ActorProvider;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sandalphon.submission.programming.BaseSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegradeProcessor;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.actor.JudgelsActorProvider;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.client.ClientChecker;
import judgels.service.jaxrs.JudgelsObjectMappers;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.LocalGitProvider;
import org.iatoki.judgels.play.general.GeneralConfig;
import org.iatoki.judgels.play.model.PlaySessionFactory;
import org.iatoki.judgels.sandalphon.lesson.LessonFs;
import org.iatoki.judgels.sandalphon.lesson.LessonGitProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFs;
import org.iatoki.judgels.sandalphon.problem.base.ProblemGitProvider;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFs;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemGraderImpl;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionServiceImpl;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponsePoller;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingResponseProcessor;
import org.iatoki.judgels.sandalphon.problem.programming.grading.ProgrammingGradingDao;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionDao;
import org.iatoki.judgels.sandalphon.problem.programming.submission.SubmissionRegrader;
import play.db.jpa.JPAApi;
import play.libs.Json;

public final class SandalphonModule extends AbstractModule {

    @Override
    public void configure() {
        Config config = ConfigFactory.load();

        GeneralConfig generalConfig = new GeneralConfig(config, "Sandalphon", UserAgent.Agent.DEFAULT_VERSION);
        bind(GeneralConfig.class).toInstance(generalConfig);

        bind(SandalphonConfiguration.class).toInstance(SandalphonProperties.build(config));

        bind(SandalphonThreadsScheduler.class).asEagerSingleton();
        bind(SandalphonSingletonsBuilder.class).asEagerSingleton();

        bind(BundleSubmissionService.class).to(BundleSubmissionServiceImpl.class);
        bind(BundleProblemGrader.class).to(BundleProblemGraderImpl.class);

        Json.setObjectMapper(objectMapper());

        bind(SessionFactory.class).to(PlaySessionFactory.class);
        bind(ActorProvider.class).to(JudgelsActorProvider.class);
        bind(Clock.class).toInstance(Clock.systemUTC());
    }

    @Provides
    @Singleton
    SubmissionStore submissionStore(
            ObjectMapper objectMapper,
            ProgrammingSubmissionDao submissionDao,
            ProgrammingGradingDao gradingDao) {
        return new BaseSubmissionStore<>(submissionDao, gradingDao, objectMapper);
    }

    @Provides
    @Singleton
    SubmissionClient submissionClient(
            SubmissionStore submissionStore,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            @Named("gabrielClientJid") String gabrielClientJid,
            ObjectMapper mapper) {

        return new SubmissionClient(
                submissionStore,
                sealtielClientAuthHeader,
                messageService,
                gabrielClientJid,
                mapper);
    }

    @Provides
    @Singleton
    SubmissionRegrader submissionRegrader(
            ActorSystem actorSystem,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        return new SubmissionRegrader(
                submissionStore,
                actorSystem.scheduler(),
                actorSystem.dispatcher(),
                processor);
    }

    @Provides
    @Singleton
    SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    GradingResponsePoller gradingResponsePoller(
            ActorSystem actorSystem,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            GradingResponseProcessor processor) {

        return new GradingResponsePoller(
                sealtielClientAuthHeader,
                messageService,
                actorSystem.scheduler(),
                actorSystem.dispatcher(),
                processor);
    }

    @Provides
    @Singleton
    GradingResponseProcessor gradingResponseProcessor(
            JPAApi jpaApi,
            ObjectMapper mapper,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            SubmissionStore submissionStore) {

        return new GradingResponseProcessor(
                jpaApi,
                mapper,
                submissionStore,
                sealtielClientAuthHeader,
                messageService);
    }

    @Provides
    @Singleton
    ClientChecker clientChecker(SandalphonConfiguration config) {
        return new ClientChecker(config.getClients());
    }

    @Provides
    @Singleton
    @ProblemFs
    FileSystem problemFs(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir(), "submissions"));
    }

    @Provides
    @Singleton
    @LessonFs
    FileSystem lessonFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    @Provides
    @Singleton
    @ProblemGitProvider
    GitProvider problemGitProvider(@ProblemFs FileSystem fs) {
        return new LocalGitProvider((LocalFileSystem) fs);
    }

    @Provides
    @Singleton
    @LessonGitProvider
    GitProvider lessonGitProvider(@LessonFs FileSystem fs) {
        return new LocalGitProvider((LocalFileSystem) fs);
    }

    private ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(
                new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule()));
    }
}
