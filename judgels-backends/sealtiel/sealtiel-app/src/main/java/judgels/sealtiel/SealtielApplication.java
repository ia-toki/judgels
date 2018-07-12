package judgels.sealtiel;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import judgels.sealtiel.rabbitmq.RabbitMQModule;
import judgels.service.jersey.JudgelsJerseyFeature;

public class SealtielApplication extends Application<SealtielApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new SealtielApplication().run(args);
    }

    @Override
    public void run(SealtielApplicationConfiguration config, Environment env) throws Exception {
        SealtielConfiguration sealtielConfig = config.getSealtielConfig();
        SealtielComponent component = DaggerSealtielComponent.builder()
                .sealtielModule(new SealtielModule(sealtielConfig))
                .rabbitMQModule(new RabbitMQModule(sealtielConfig.getRabbitMQConfig()))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.versionResource());
        env.jersey().register(component.messageResource());

        env.healthChecks().register("rabbitmq", component.rabbitmqHealthCheck());
    }
}
