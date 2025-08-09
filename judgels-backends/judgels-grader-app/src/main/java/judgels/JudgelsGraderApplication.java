package judgels;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import judgels.gabriel.DaggerGabrielComponent;
import judgels.gabriel.GabrielComponent;
import judgels.gabriel.GabrielConfiguration;
import judgels.gabriel.JudgelsGraderModule;
import judgels.gabriel.cache.CacheModule;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsScheduler;

public class JudgelsGraderApplication extends Application<JudgelsGraderApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new JudgelsGraderApplication().run(args);
    }

    @Override
    public void run(JudgelsGraderApplicationConfiguration config, Environment env) throws Exception {
        JudgelsScheduler scheduler = new JudgelsScheduler(env.lifecycle());
        runGabriel(config, env, scheduler);
    }

    private void runGabriel(JudgelsGraderApplicationConfiguration config, Environment env, JudgelsScheduler scheduler) {
        JudgelsGraderConfiguration judgelsConfig = config.getJudgelsConfig();
        GabrielConfiguration gabrielConfig = config.getGabrielConfig();

        GabrielComponent component = DaggerGabrielComponent.builder()
                .judgelsGraderModule(new JudgelsGraderModule(judgelsConfig))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .isolateModule(new IsolateModule(gabrielConfig.getIsolateConfig()))
                .gradingModule(new GradingModule(env.lifecycle(), gabrielConfig.getGradingConfig()))
                .cacheModule(new CacheModule(gabrielConfig.getCacheConfig()))
                .build();

        scheduler.scheduleOnce(
                "gabriel-grading-request-poller",
                component.gradingRequestPoller());
    }
}
