package judgels.uriel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.time.Duration;
import judgels.fs.aws.AwsModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jaxrs.JudgelsObjectMappers;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.uriel.file.FileModule;
import judgels.uriel.gabriel.GabrielModule;
import judgels.uriel.hibernate.UrielHibernateBundle;
import judgels.uriel.jophiel.JophielModule;
import judgels.uriel.messaging.MessagingModule;
import judgels.uriel.sandalphon.SandalphonModule;
import judgels.uriel.submission.programming.SubmissionModule;

public class UrielApplication extends Application<UrielApplicationConfiguration> {
    private final HibernateBundle<UrielApplicationConfiguration> hibernateBundle = new UrielHibernateBundle();

    public static void main(String[] args) throws Exception {
        new UrielApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<UrielApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new UrielMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(UrielApplicationConfiguration config, Environment env) {
        UrielConfiguration urielConfig = config.getUrielConfig();
        UrielComponent component = DaggerUrielComponent.builder()
                .awsModule(new AwsModule(urielConfig.getAwsConfig()))
                .fileModule(new FileModule(urielConfig.getFileConfig()))
                .jophielModule(new JophielModule(urielConfig.getJophielConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new SandalphonModule(urielConfig.getSandalphonConfig()))
                .gabrielModule(new GabrielModule(urielConfig.getGabrielConfig()))
                .messagingModule(new MessagingModule(urielConfig.getRabbitMQConfig()))
                .submissionModule(new SubmissionModule(urielConfig.getSubmissionConfig()))
                .urielModule(new UrielModule(urielConfig))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.contestResource());
        env.jersey().register(component.contestWebResource());
        env.jersey().register(component.contestAnnouncementResource());
        env.jersey().register(component.contestClarificationResource());
        env.jersey().register(component.contestContestantResource());
        env.jersey().register(component.contestEditorialResource());
        env.jersey().register(component.contestFileResource());
        env.jersey().register(component.contestHistoryResource());
        env.jersey().register(component.contestLogResource());
        env.jersey().register(component.contestManagerResource());
        env.jersey().register(component.contestModuleResource());
        env.jersey().register(component.contestProblemResource());
        env.jersey().register(component.contestScoreboardResource());
        env.jersey().register(component.contestProgrammingSubmissionResource());
        env.jersey().register(component.contestBundleSubmissionResource());
        env.jersey().register(component.contestSupervisorResource());
        env.jersey().register(component.contestRatingResource());
        env.jersey().register(component.pingResource());

        component.scheduler().scheduleWithFixedDelay(
                "contest-scoreboard-poller",
                component.contestScoreboardPoller(),
                Duration.ofSeconds(10));

        component.scheduler().scheduleOnce(
                "contest-log-poller",
                component.contestLogPoller());

        if (urielConfig.getRabbitMQConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "grading-response-poller",
                    component.gradingResponsePoller());
        }
    }
}
