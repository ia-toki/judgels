package judgels.service;

import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JudgelsScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JudgelsScheduler.class);

    private final LifecycleEnvironment lifecycleEnvironment;

    @Inject
    public JudgelsScheduler(LifecycleEnvironment lifecycleEnvironment) {
        this.lifecycleEnvironment = lifecycleEnvironment;
    }

    public void scheduleWithFixedDelay(String name, Runnable job, Duration initialDelay, Duration delay) {
        ScheduledExecutorService executorService = lifecycleEnvironment.scheduledExecutorService(name).build();

        LOGGER.info("Scheduling job {}", name);
        executorService.scheduleWithFixedDelay(
                job,
                initialDelay.getSeconds(),
                delay.getSeconds(),
                TimeUnit.SECONDS);
    }
}
