package judgels.jophiel;

import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature;
import io.dropwizard.Application;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.optional.EmptyOptionalException;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import judgels.jophiel.hibernate.JophielHibernateBundle;
import judgels.jophiel.hibernate.JophielHibernateModule;

public class JophielApplication extends Application<JophielApplicationConfiguration> {
    private final HibernateBundle<JophielApplicationConfiguration> hibernateBundle = new JophielHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JophielApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JophielApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new JophielMigrationsBundle());
    }

    @Override
    public void run(JophielApplicationConfiguration config, Environment env) throws Exception {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE);
        env.jersey().register(new EmptyOptionalExceptionMapper());

        JophielComponent component = DaggerJophielComponent.builder()
                .jophielHibernateModule(new JophielHibernateModule(hibernateBundle.getSessionFactory()))
                .build();

        env.jersey().register(component.versionResource());
        env.jersey().register(component.userResource());
    }

    // https://github.com/palantir/http-remoting/issues/427
    @Provider
    private static class EmptyOptionalExceptionMapper implements ExceptionMapper<EmptyOptionalException> {
        @Override
        public Response toResponse(EmptyOptionalException exception) {
            return Response.noContent().build();
        }
    }
}
