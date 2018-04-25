package judgels.uriel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.uriel.hibernate.UrielHibernateBundle;
import judgels.uriel.hibernate.UrielHibernateModule;
import judgels.uriel.jophiel.JophielModule;

public class UrielApplication extends Application<UrielApplicationConfiguration> {
    private final HibernateBundle<UrielApplicationConfiguration> hibernateBundle = new UrielHibernateBundle();

    public static void main(String[] args) throws Exception {
        new UrielApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<UrielApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(UrielApplicationConfiguration config, Environment env) {
        UrielConfiguration urielConfig = config.getUrielConfig();
        UrielComponent component = DaggerUrielComponent.builder()
                .jophielModule(new JophielModule(urielConfig.getJophielConfig()))
                .urielHibernateModule(new UrielHibernateModule(hibernateBundle))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.contestResource());
        env.jersey().register(component.contestAnnouncementResource());
        env.jersey().register(component.contestScoreboardResource());
        env.jersey().register(component.contestContestantResource());
        env.jersey().register(component.versionResource());
    }
}
