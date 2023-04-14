package judgels;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import judgels.gabriel.DaggerGabrielComponent;
import judgels.gabriel.GabrielComponent;
import judgels.gabriel.GabrielConfiguration;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.messaging.MessagingModule;
import judgels.gabriel.moe.MoeModule;
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
                .gradingModule(new GradingModule(judgelsConfig.getBaseDataDir(), env.lifecycle(), gabrielConfig.getGradingConfig()))
                .messagingModule(new MessagingModule(judgelsConfig.getRabbitMQConfig()))
                .moeModule(new MoeModule(gabrielConfig.getMoeConfig()))
                .build();

        scheduler.scheduleOnce(
                "gabriel-grading-request-poller",
                component.gradingRequestPoller());
    }
}
