package judgels.gabriel;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.moe.MoeModule;
import judgels.gabriel.sealtiel.SealtielModule;
import judgels.service.JudgelsApplicationModule;

public class GabrielApplication extends Application<GabrielApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new GabrielApplication().run(args);
    }

    @Override
    public void run(GabrielApplicationConfiguration configuration, Environment environment) {
        GabrielConfiguration gabrielConfig = configuration.getGabrielConfig();
        GabrielComponent component = DaggerGabrielComponent.builder()
                .judgelsApplicationModule(new JudgelsApplicationModule(environment))
                .gabrielModule(new GabrielModule(gabrielConfig))
                .gradingModule(new GradingModule(gabrielConfig.getGradingWorkerConfig()))
                .moeModule(new MoeModule(gabrielConfig.getMoeConfig()))
                .sealtielModule(new SealtielModule(gabrielConfig.getSealtielConfig()))
                .build();

        component.scheduler().scheduleOnce(
                "grading-request-poller",
                component.gradingRequestPoller());
    }
}
