package org.iatoki.judgels.sandalphon;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.persistence.ActorProvider;
import judgels.service.JudgelsVersion;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.LocalFileSystemProvider;
import org.iatoki.judgels.LocalGitProvider;
import org.iatoki.judgels.jophiel.JophielAuthAPI;
import org.iatoki.judgels.jophiel.user.BaseUserService;
import org.iatoki.judgels.play.JudgelsPlayProperties;
import org.iatoki.judgels.play.general.GeneralName;
import org.iatoki.judgels.play.general.GeneralVersion;
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
import org.iatoki.judgels.sandalphon.user.UserServiceImpl;

import java.time.Clock;

public final class SandalphonModule extends AbstractModule {

    @Override
    public void configure() {
        bindConstant().annotatedWith(GeneralName.class).to("Sandalphon");
        bindConstant().annotatedWith(GeneralVersion.class).to(JudgelsVersion.INSTANCE);

        // <DEPRECATED>
        Config config = ConfigFactory.load();
        JudgelsPlayProperties.buildInstance("Sandalphon", JudgelsVersion.INSTANCE, config);
        SandalphonProperties.buildInstance(config);
        bind(SandalphonSingletonsBuilder.class).asEagerSingleton();
        // </DEPRECATED>

        bind(JudgelsDataMigrator.class).to(SandalphonDataMigrator.class);

        bind(ProgrammingSubmissionService.class).to(ProgrammingSubmissionServiceImpl.class);
        bind(BundleSubmissionService.class).to(BundleSubmissionServiceImpl.class);
        bind(BundleProblemGrader.class).to(BundleProblemGraderImpl.class);

        bind(JophielAuthAPI.class).toInstance(jophielAuthAPI());
        bind(FileSystemProvider.class).annotatedWith(ProblemFileSystemProvider.class).toInstance(problemFileSystemProvider());
        bind(FileSystemProvider.class).annotatedWith(SubmissionFileSystemProvider.class).toInstance(submissionFileSystemProvider());
        bind(FileSystemProvider.class).annotatedWith(LessonFileSystemProvider.class).toInstance(lessonFileSystemProvider());
        bind(GitProvider.class).annotatedWith(ProblemGitProvider.class).toInstance(problemGitProvider());
        bind(GitProvider.class).annotatedWith(LessonGitProvider.class).toInstance(lessonGitProvider());
        bind(BaseUserService.class).to(UserServiceImpl.class);

        bind(SessionFactory.class).to(LegacySessionFactory.class);
        bind(ActorProvider.class).to(LegacyActorProvider.class);
        bind(Clock.class).toInstance(Clock.systemUTC());
    }

    private SandalphonProperties sandalphonProperties() {
        return SandalphonProperties.getInstance();
    }

    private JophielAuthAPI jophielAuthAPI() {
        return new JophielAuthAPI(sandalphonProperties().getRaphaelBaseUrl(), sandalphonProperties().getJophielBaseUrl());
    }

    private LocalFileSystemProvider problemFileSystemProvider() {
        return new LocalFileSystemProvider(sandalphonProperties().getProblemLocalDir());
    }

    private FileSystemProvider submissionFileSystemProvider() {
        return new LocalFileSystemProvider(sandalphonProperties().getSubmissionLocalDir());
    }

    private LocalFileSystemProvider lessonFileSystemProvider() {
        return new LocalFileSystemProvider(sandalphonProperties().getLessonLocalDir());
    }

    private GitProvider problemGitProvider() {
        return new LocalGitProvider(problemFileSystemProvider());
    }

    private GitProvider lessonGitProvider() {
        return new LocalGitProvider(lessonFileSystemProvider());
    }
}
