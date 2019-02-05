package judgels.jerahmeel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.jerahmeel.hibernate.JerahmeelHibernateBundle;
import judgels.jerahmeel.jophiel.JophielModule;
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
                // .awsModule(new AwsModule(jerahmeelConfig.getAwsConfig()))
                // .fileModule(new FileModule(jerahmeelConfig.getFileConfig()))
                // .gabrielModule(new GabrielModule(jerahmeelConfig.getGabrielConfig()))
                .jophielModule(new JophielModule(jerahmeelConfig.getJophielConfig()))
                // .judgelsApplicationModule(new JudgelsApplicationModule(env))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                // .sandalphonModule(new SandalphonModule(jerahmeelConfig.getSandalphonConfig()))
                // .sealtielModule(new SealtielModule(jerahmeelConfig.getSealtielConfig()))
                // .submissionModule(new SubmissionModule(jerahmeelConfig.getSubmissionConfig()))
                .jerahmeelModule(new JerahmeelModule(jerahmeelConfig))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.adminResource());
        // env.jersey().register(component.contestResource());
        // env.jersey().register(component.contestWebResource());
        // env.jersey().register(component.contestAnnouncementResource());
        // env.jersey().register(component.contestClarificationResource());
        // env.jersey().register(component.contestContestantResource());
        // env.jersey().register(component.contestFileResource());
        // env.jersey().register(component.contestManagerResource());
        // env.jersey().register(component.contestModuleResource());
        // env.jersey().register(component.contestProblemResource());
        // env.jersey().register(component.contestScoreboardResource());
        // env.jersey().register(component.contestSubmissionResource());
        // env.jersey().register(component.contestSupervisorResource());
        // env.jersey().register(component.contestRatingResource());
        env.jersey().register(component.versionResource());
    }
}
