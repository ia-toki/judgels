package judgels.uriel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.service.jersey.JudgelsJerseyFeature;
import judgels.uriel.hibernate.UrielHibernateBundle;
import judgels.uriel.hibernate.UrielHibernateModule;

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
    public void run(UrielApplicationConfiguration config, Environment env) throws Exception {
        UrielComponent component = DaggerUrielComponent.builder()
                .urielHibernateModule(new UrielHibernateModule(hibernateBundle))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.contestResource());
        env.jersey().register(component.contestScoreboardResource());
        env.jersey().register(component.contestContestantResource());
    }
}
