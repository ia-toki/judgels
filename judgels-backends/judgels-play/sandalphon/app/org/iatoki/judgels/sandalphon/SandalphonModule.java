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
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.client.ClientChecker;
import judgels.service.jaxrs.JudgelsObjectMappers;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.LocalGitProvider;
import org.iatoki.judgels.play.general.GeneralConfig;
import org.iatoki.judgels.play.migration.DataMigrationInit;
import org.iatoki.judgels.play.migration.JudgelsDataMigrator;
import org.iatoki.judgels.play.model.LegacyActorProvider;
import org.iatoki.judgels.play.model.LegacySessionFactory;
import org.iatoki.judgels.sandalphon.lesson.LessonFileSystemProvider;
import org.iatoki.judgels.sandalphon.lesson.LessonGitProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemFileSystemProvider;
import org.iatoki.judgels.sandalphon.problem.base.ProblemGitProvider;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFileSystemProvider;
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

        SandalphonConfiguration sandalphonConfig = SandalphonProperties.build(config);
        bind(SandalphonConfiguration.class).toInstance(sandalphonConfig);

        bind(SandalphonThreadsScheduler.class).asEagerSingleton();
        bind(SandalphonSingletonsBuilder.class).asEagerSingleton();
        bind(DataMigrationInit.class).asEagerSingleton();

        bind(JudgelsDataMigrator.class).to(SandalphonDataMigrator.class);

        bind(BundleSubmissionService.class).to(BundleSubmissionServiceImpl.class);
        bind(BundleProblemGrader.class).to(BundleProblemGraderImpl.class);

        bind(ClientChecker.class).toInstance(clientChecker(sandalphonConfig));

        bind(FileSystem.class).annotatedWith(ProblemFileSystemProvider.class).toInstance(problemFileSystemProvider(sandalphonConfig));
        bind(FileSystem.class).annotatedWith(SubmissionFileSystemProvider.class).toInstance(submissionFileSystemProvider(sandalphonConfig));
        bind(FileSystem.class).annotatedWith(LessonFileSystemProvider.class).toInstance(lessonFileSystemProvider(sandalphonConfig));
        bind(GitProvider.class).annotatedWith(ProblemGitProvider.class).toInstance(problemGitProvider(sandalphonConfig));
        bind(GitProvider.class).annotatedWith(LessonGitProvider.class).toInstance(lessonGitProvider(sandalphonConfig));

        Json.setObjectMapper(objectMapper());

        bind(SessionFactory.class).to(LegacySessionFactory.class);
        bind(ActorProvider.class).to(LegacyActorProvider.class);
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
    SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFileSystemProvider FileSystem submissionFs) {
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

    private ClientChecker clientChecker(SandalphonConfiguration config) {
        return new ClientChecker(config.getClients());
    }

    private LocalFileSystem problemFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    private FileSystem submissionFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir(), "submissions"));
    }

    private LocalFileSystem lessonFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    private GitProvider problemGitProvider(SandalphonConfiguration config) {
        return new LocalGitProvider(problemFileSystemProvider(config));
    }

    private GitProvider lessonGitProvider(SandalphonConfiguration config) {
        return new LocalGitProvider(lessonFileSystemProvider(config));
    }

    private ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(
                new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule()));
    }
}
