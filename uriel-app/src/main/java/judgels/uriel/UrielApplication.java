package judgels.uriel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.file.Path;
import java.nio.file.Paths;
import judgels.fs.aws.AwsModule;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.service.jersey.JudgelsObjectMappers;
import judgels.uriel.gabriel.GabrielModule;
import judgels.uriel.hibernate.UrielHibernateBundle;
import judgels.uriel.hibernate.UrielHibernateModule;
import judgels.uriel.jophiel.JophielModule;
import judgels.uriel.sandalphon.SandalphonModule;
import judgels.uriel.sealtiel.SealtielModule;
import judgels.uriel.submission.SubmissionModule;

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
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(UrielApplicationConfiguration config, Environment env) {
        UrielConfiguration urielConfig = config.getUrielConfig();
        Path baseDataDir = Paths.get(urielConfig.getBaseDataDir());

        UrielComponent component = DaggerUrielComponent.builder()
                .jophielModule(new JophielModule(urielConfig.getJophielConfig()))
                .sandalphonModule(new SandalphonModule(urielConfig.getSandalphonConfig()))
                .sealtielModule(new SealtielModule(urielConfig.getSealtielConfig()))
                .gabrielModule(new GabrielModule(urielConfig.getGabrielConfig()))
                .urielHibernateModule(new UrielHibernateModule(hibernateBundle))
                .awsModule(new AwsModule(urielConfig.getAwsConfig()))
                .submissionModule(new SubmissionModule(baseDataDir, urielConfig.getSubmissionConfig()))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.contestResource());
        env.jersey().register(component.contestWebResource());
        env.jersey().register(component.contestAnnouncementResource());
        env.jersey().register(component.contestProblemResource());
        env.jersey().register(component.contestScoreboardResource());
        env.jersey().register(component.contestContestantResource());
        env.jersey().register(component.contestSubmissionResource());
        env.jersey().register(component.versionResource());
    }
}
