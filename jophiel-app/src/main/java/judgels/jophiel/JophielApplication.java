package judgels.jophiel;

import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature;
import io.dropwizard.Application;
import io.dropwizard.jersey.optional.EmptyOptionalException;
import io.dropwizard.setup.Environment;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public class JophielApplication extends Application<JophielApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new JophielApplication().run(args);
    }

    @Override
    public void run(JophielApplicationConfiguration config, Environment env) throws Exception {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE);
        env.jersey().register(new EmptyOptionalExceptionMapper());

        JophielComponent component = DaggerJophielComponent.create();

        env.jersey().register(component.versionResource());
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
