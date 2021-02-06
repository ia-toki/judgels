package judgels.jerahmeel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.gabriel.GabrielModule;
import judgels.jerahmeel.hibernate.JerahmeelHibernateBundle;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.jerahmeel.sandalphon.SandalphonModule;
import judgels.jerahmeel.sealtiel.SealtielModule;
import judgels.jerahmeel.submission.programming.SubmissionModule;
import judgels.jerahmeel.uriel.UrielModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jaxrs.JudgelsObjectMappers;
import judgels.service.jersey.JudgelsJerseyFeature;

public class JerahmeelApplication extends Application<JerahmeelApplicationConfiguration> {
    private final HibernateBundle<JerahmeelApplicationConfiguration> hibernateBundle = new JerahmeelHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JerahmeelApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JerahmeelApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new JerahmeelMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(JerahmeelApplicationConfiguration config, Environment env) throws Exception {
        JerahmeelConfiguration jerahmeelConfig = config.getJerahmeelConfig();
        JerahmeelComponent component = DaggerJerahmeelComponent.builder()
                .awsModule(new AwsModule(jerahmeelConfig.getAwsConfig()))
                .gabrielModule(new GabrielModule(jerahmeelConfig.getGabrielConfig()))
                .jophielModule(new JophielModule(jerahmeelConfig.getJophielConfig()))
                .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new SandalphonModule(jerahmeelConfig.getSandalphonConfig()))
                .sealtielModule(new SealtielModule(jerahmeelConfig.getSealtielConfig()))
                .urielModule(new UrielModule(jerahmeelConfig.getUrielConfig()))
                .submissionModule(new SubmissionModule(
                        jerahmeelConfig.getSubmissionConfig(),
                        jerahmeelConfig.getStatsConfig()))
                .jerahmeelModule(new JerahmeelModule(jerahmeelConfig))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.archiveResource());
        env.jersey().register(component.courseResource());
        env.jersey().register(component.chapterResource());
        env.jersey().register(component.courseChapterResource());
        env.jersey().register(component.chapterLessonResource());
        env.jersey().register(component.chapterProblemResource());
        env.jersey().register(component.problemSetResource());
        env.jersey().register(component.problemSetProblemResource());
        env.jersey().register(component.itemSubmissionResource());
        env.jersey().register(component.submissionResource());
        env.jersey().register(component.userStatsResource());
        env.jersey().register(component.pingResource());

        if (jerahmeelConfig.getSealtielConfig().isPresent()) {
            component.scheduler().scheduleOnce(
                    "grading-response-poller",
                    component.gradingResponsePoller());
        }

        env.admin().addTask(component.statsTask());
    }
}
