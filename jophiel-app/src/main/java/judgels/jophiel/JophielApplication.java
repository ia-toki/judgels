package judgels.jophiel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.jophiel.hibernate.JophielHibernateBundle;
import judgels.jophiel.hibernate.JophielHibernateModule;
import judgels.jophiel.mailer.MailerModule;
import judgels.jophiel.user.master.MasterUsersModule;
import judgels.jophiel.web.WebConfiguration;
import judgels.jophiel.web.WebModule;
import judgels.service.jersey.JudgelsJerseyFeature;

public class JophielApplication extends Application<JophielApplicationConfiguration> {
    private final HibernateBundle<JophielApplicationConfiguration> hibernateBundle = new JophielHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JophielApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JophielApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new JophielMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(JophielApplicationConfiguration config, Environment env) throws Exception {
        JophielConfiguration jophielConfig = config.getJophielConfig();
        JophielComponent component = DaggerJophielComponent.builder()
                .jophielHibernateModule(new JophielHibernateModule(hibernateBundle))
                .masterUsersModule(new MasterUsersModule(jophielConfig.getMasterUsers()))
                .mailerModule(new MailerModule(jophielConfig.getMailerConfig()))
                .webModule(new WebModule(WebConfiguration.fromServerConfig(jophielConfig)))
                .build();

        component.masterUsersCreator().create();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.accountResource());
        env.jersey().register(component.userResource());
        env.jersey().register(component.webResource());
        env.jersey().register(component.versionResource());
    }
}
