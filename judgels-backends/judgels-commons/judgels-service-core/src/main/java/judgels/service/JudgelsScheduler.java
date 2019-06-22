package judgels.service;

import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
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

    public void scheduleOnce(String name, Runnable job) {
        ExecutorService executorService = lifecycleEnvironment.executorService(name)
                .minThreads(1)
                .maxThreads(1)
                .build();

        LOGGER.info("Scheduling job {}", name);
        executorService.submit(job);
    }

    public void scheduleWithFixedDelay(String name, Runnable job, Duration delay) {
        ScheduledExecutorService executorService = lifecycleEnvironment.scheduledExecutorService(name)
                .removeOnCancelPolicy(true)
                .build();

        LOGGER.info("Scheduling job {}", name);
        executorService.scheduleWithFixedDelay(
                job,
                2,
                delay.getSeconds(),
                TimeUnit.SECONDS);
    }
}
