package judgels;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import judgels.gabriel.DaggerGabrielComponent;
import judgels.gabriel.GabrielComponent;
import judgels.gabriel.JudgelsGraderModule;
import judgels.gabriel.grading.GradingModule;
import judgels.grading.CacheModule;
import judgels.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;

public class JudgelsGraderApplication extends Application<JudgelsGraderApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new JudgelsGraderApplication().run(args);
    }

    @Override
    public void run(JudgelsGraderApplicationConfiguration config, Environment env) throws Exception {
        runGabriel(config, env);
    }

    private void runGabriel(JudgelsGraderApplicationConfiguration config, Environment env) {
        JudgelsGraderConfiguration judgelsConfig = config.getJudgelsConfig();

        GabrielComponent component = DaggerGabrielComponent.builder()
                .judgelsGraderModule(new JudgelsGraderModule(judgelsConfig))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .isolateModule(new IsolateModule(judgelsConfig.getIsolateConfig()))
                .gradingModule(new GradingModule(env.lifecycle(), judgelsConfig.getGradingConfig()))
                .cacheModule(new CacheModule(judgelsConfig.getGradingConfig().getCacheConfig()))
                .build();

        env.lifecycle().manage(component.gradingRequestPoller());
    }
}
