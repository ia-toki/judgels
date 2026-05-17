package judgels;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import judgels.grading.CacheModule;
import judgels.grading.GradingModule;
import judgels.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;

public class JudgelsGraderApplication extends Application<JudgelsGraderApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new JudgelsGraderApplication().run(args);
    }

    @Override
    public void run(JudgelsGraderApplicationConfiguration config, Environment env) throws Exception {
        runGrader(config, env);
    }

    private void runGrader(JudgelsGraderApplicationConfiguration config, Environment env) {
        JudgelsGraderConfiguration judgelsConfig = config.getJudgelsConfig();

        JudgelsGraderComponent component = DaggerJudgelsGraderComponent.builder()
                .judgelsGraderModule(new JudgelsGraderModule(judgelsConfig))
                .rabbitMQModule(new RabbitMQModule(judgelsConfig.getRabbitMQConfig()))
                .isolateModule(new IsolateModule(judgelsConfig.getIsolateConfig()))
                .gradingModule(new GradingModule(env.lifecycle(), judgelsConfig.getGradingConfig()))
                .cacheModule(new CacheModule(judgelsConfig.getGradingConfig().getCacheConfig()))
                .build();

        env.lifecycle().manage(component.gradingRequestPoller());
    }
}
