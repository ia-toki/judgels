package org.iatoki.judgels.sandalphon;

import com.google.inject.AbstractModule;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.service.client.ClientChecker;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.LocalFileSystemProvider;
import org.iatoki.judgels.LocalGitProvider;
import org.iatoki.judgels.play.general.GeneralConfig;
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
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionServiceImpl;

public final class SandalphonModule extends AbstractModule {

    @Override
    public void configure() {
        Config config = ConfigFactory.load();

        GeneralConfig generalConfig = new GeneralConfig(config, "Sandalphon", UserAgent.Agent.DEFAULT_VERSION);
        bind(GeneralConfig.class).toInstance(generalConfig);

        SandalphonConfiguration sandalphonConfig = SandalphonProperties.build(config);
        bind(SandalphonConfiguration.class).toInstance(sandalphonConfig);

        bind(SandalphonSingletonsBuilder.class).asEagerSingleton();

        bind(JudgelsDataMigrator.class).to(SandalphonDataMigrator.class);

        bind(ProgrammingSubmissionService.class).to(ProgrammingSubmissionServiceImpl.class);
        bind(BundleSubmissionService.class).to(BundleSubmissionServiceImpl.class);
        bind(BundleProblemGrader.class).to(BundleProblemGraderImpl.class);

        bind(ClientChecker.class).toInstance(clientChecker(sandalphonConfig));

        bind(FileSystemProvider.class).annotatedWith(ProblemFileSystemProvider.class).toInstance(problemFileSystemProvider(sandalphonConfig));
        bind(FileSystemProvider.class).annotatedWith(SubmissionFileSystemProvider.class).toInstance(submissionFileSystemProvider(sandalphonConfig));
        bind(FileSystemProvider.class).annotatedWith(LessonFileSystemProvider.class).toInstance(lessonFileSystemProvider(sandalphonConfig));
        bind(GitProvider.class).annotatedWith(ProblemGitProvider.class).toInstance(problemGitProvider(sandalphonConfig));
        bind(GitProvider.class).annotatedWith(LessonGitProvider.class).toInstance(lessonGitProvider(sandalphonConfig));

        bind(SessionFactory.class).to(LegacySessionFactory.class);
        bind(ActorProvider.class).to(LegacyActorProvider.class);
        bind(Clock.class).toInstance(Clock.systemUTC());
    }

    private ClientChecker clientChecker(SandalphonConfiguration config) {
        return new ClientChecker(config.getClients());
    }

    private LocalFileSystemProvider problemFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystemProvider(new File(config.getBaseDataDir()));
    }

    private FileSystemProvider submissionFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystemProvider(new File(config.getBaseDataDir(), "submissions"));
    }

    private LocalFileSystemProvider lessonFileSystemProvider(SandalphonConfiguration config) {
        return new LocalFileSystemProvider(new File(config.getBaseDataDir()));
    }

    private GitProvider problemGitProvider(SandalphonConfiguration config) {
        return new LocalGitProvider(problemFileSystemProvider(config));
    }

    private GitProvider lessonGitProvider(SandalphonConfiguration config) {
        return new LocalGitProvider(lessonFileSystemProvider(config));
    }
}
