package judgels.sealtiel;

import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import judgels.sealtiel.rabbitmq.RabbitMQModule;

public class SealtielApplication extends Application<SealtielApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new SealtielApplication().run(args);
    }

    @Override
    public void run(SealtielApplicationConfiguration config, Environment env) throws Exception {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE);

        SealtielConfiguration sealtielConfig = config.getSealtielConfig();
        SealtielComponent component = DaggerSealtielComponent.builder()
                .sealtielModule(new SealtielModule(sealtielConfig))
                .rabbitMQModule(new RabbitMQModule(sealtielConfig.getRabbitMQConfig()))
                .build();

        env.jersey().register(component.messageResource());
    }
}
