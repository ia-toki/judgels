package org.iatoki.judgels.jerahmeel;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;
import org.iatoki.judgels.AWSFileSystemProvider;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.LocalFileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonFactory;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionServiceImpl;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionServiceImpl;
import org.iatoki.judgels.jerahmeel.user.UserServiceImpl;
import org.iatoki.judgels.jophiel.JophielAuthAPI;
import org.iatoki.judgels.jophiel.user.BaseUserService;
import org.iatoki.judgels.play.general.GeneralName;
import org.iatoki.judgels.play.general.GeneralVersion;
import org.iatoki.judgels.play.migration.JudgelsDataMigrator;
import org.iatoki.judgels.play.model.LegacyActorProvider;
import org.iatoki.judgels.play.model.LegacySessionFactory;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.SandalphonBundleProblemGrader;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;

import java.time.Clock;

public final class JerahmeelModule extends AbstractModule {

    @Override
    public void configure() {
        bindConstant().annotatedWith(GeneralName.class).to("Jerahmeel");
        bindConstant().annotatedWith(GeneralVersion.class).to(UserAgent.Agent.DEFAULT_VERSION);

        // <DEPRECATED>
        Config config = ConfigFactory.load();
        JerahmeelProperties.buildInstance(config);
        bind(JerahmeelSingletonsBuilder.class).asEagerSingleton();
        // </DEPRECATED>

        bind(JudgelsDataMigrator.class).to(JerahmeelDataMigrator.class);

        bind(BundleSubmissionService.class).to(BundleSubmissionServiceImpl.class);
        bind(ProgrammingSubmissionService.class).to(ProgrammingSubmissionServiceImpl.class);

        bind(JophielAuthAPI.class).toInstance(jophielAuthAPI());
        bind(SandalphonClientAPI.class).toInstance(sandalphonClientAPI());

        bind(FileSystemProvider.class).annotatedWith(BundleSubmissionLocalFileSystemProvider.class).toInstance(bundleSubmissionLocalFileSystemProvider());

        FileSystemProvider bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider();
        if (bundleSubmissionRemoteFileSystemProvider != null) {
            bind(FileSystemProvider.class).annotatedWith(BundleSubmissionRemoteFileSystemProvider.class).toInstance(bundleSubmissionRemoteFileSystemProvider);
        } else {
            bind(FileSystemProvider.class).annotatedWith(BundleSubmissionRemoteFileSystemProvider.class).toProvider(Providers.of(null));
        }

        bind(FileSystemProvider.class).annotatedWith(ProgrammingSubmissionLocalFileSystemProvider.class).toInstance(submissionLocalFileSystemProvider());

        FileSystemProvider submissionRemoteFileSystemProvider = submissionRemoteFileSystemProvider();
        if (submissionRemoteFileSystemProvider != null) {
            bind(FileSystemProvider.class).annotatedWith(ProgrammingSubmissionRemoteFileSystemProvider.class).toInstance(submissionRemoteFileSystemProvider);
        } else {
            bind(FileSystemProvider.class).annotatedWith(ProgrammingSubmissionRemoteFileSystemProvider.class).toProvider(Providers.of(null));
        }

        bind(BaseUserService.class).to(UserServiceImpl.class);
        bind(BundleProblemGrader.class).to(SandalphonBundleProblemGrader.class);

        bind(SessionFactory.class).to(LegacySessionFactory.class);
        bind(ActorProvider.class).to(LegacyActorProvider.class);
        bind(Clock.class).toInstance(Clock.systemUTC());
    }

    private JerahmeelProperties jerahmeelProperties() {
        return JerahmeelProperties.getInstance();
    }

    private JophielAuthAPI jophielAuthAPI() {
        return new JophielAuthAPI(jerahmeelProperties().getRaphaelBaseUrl(), jerahmeelProperties().getJophielBaseUrl());
    }

    private SandalphonClientAPI sandalphonClientAPI() {
        return SandalphonFactory.createSandalphon(jerahmeelProperties().getSandalphonBaseUrl()).connectToClientAPI(jerahmeelProperties().getSandalphonClientJid(), jerahmeelProperties().getSandalphonClientSecret());
    }

    private FileSystemProvider bundleSubmissionRemoteFileSystemProvider() {
        FileSystemProvider bundleSubmissionRemoteFileSystemProvider = null;
        if (jerahmeelProperties().isSubmissionUsingAWSS3()) {
            AmazonS3Client awsS3Client;
            if (jerahmeelProperties().isSubmissionAWSUsingKeys()) {
                awsS3Client = new AmazonS3Client(new BasicAWSCredentials(jerahmeelProperties().getSubmissionAWSAccessKey(), jerahmeelProperties().getSubmissionAWSSecretKey()));
            } else {
                awsS3Client = new AmazonS3Client();
            }
            bundleSubmissionRemoteFileSystemProvider = new AWSFileSystemProvider(awsS3Client, jerahmeelProperties().getSubmissionAWSS3BucketName(), jerahmeelProperties().getSubmissionAWSS3BucketRegion());
        }

        return bundleSubmissionRemoteFileSystemProvider;
    }

    private FileSystemProvider bundleSubmissionLocalFileSystemProvider() {
        return new LocalFileSystemProvider(jerahmeelProperties().getSubmissionLocalDir());
    }

    private FileSystemProvider submissionRemoteFileSystemProvider() {
        FileSystemProvider submissionRemoteFileSystemProvider = null;
        if (jerahmeelProperties().isSubmissionUsingAWSS3()) {
            AmazonS3Client awsS3Client;
            if (jerahmeelProperties().isSubmissionAWSUsingKeys()) {
                awsS3Client = new AmazonS3Client(new BasicAWSCredentials(jerahmeelProperties().getSubmissionAWSAccessKey(), jerahmeelProperties().getSubmissionAWSSecretKey()));
            } else {
                awsS3Client = new AmazonS3Client();
            }
            submissionRemoteFileSystemProvider = new AWSFileSystemProvider(awsS3Client, jerahmeelProperties().getSubmissionAWSS3BucketName(), jerahmeelProperties().getSubmissionAWSS3BucketRegion());
        }

        return submissionRemoteFileSystemProvider;
    }

    private FileSystemProvider submissionLocalFileSystemProvider() {
        return new LocalFileSystemProvider(jerahmeelProperties().getSubmissionLocalDir());
    }
}
