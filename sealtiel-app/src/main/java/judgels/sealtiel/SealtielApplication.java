package judgels.sealtiel;

import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature;
import io.dropwizard.Application;
import io.dropwizard.jersey.optional.EmptyOptionalException;
import io.dropwizard.setup.Environment;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import judgels.sealtiel.rabbitmq.RabbitMQModule;

public class SealtielApplication extends Application<SealtielApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new SealtielApplication().run(args);
    }

    @Override
    public void run(SealtielApplicationConfiguration config, Environment env) throws Exception {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE);
        env.jersey().register(new EmptyOptionalExceptionMapper());

        SealtielConfiguration sealtielConfig = config.getSealtielConfig();
        SealtielComponent component = DaggerSealtielComponent.builder()
                .sealtielModule(new SealtielModule(sealtielConfig))
                .rabbitMQModule(new RabbitMQModule(sealtielConfig.getRabbitMQConfig()))
                .build();

        env.jersey().register(component.messageResource());
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
