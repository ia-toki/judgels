package judgels.service;

import io.dropwizard.setup.Environment;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JudgelsApplicationUtils {
    private JudgelsApplicationUtils() {}

    public static void scheduleJobWithFixedDelay(
            Runnable job,
            Environment env,
            Duration initialDelay,
            Duration delay) {
        String nameFormat = job.getClass().getName();
        ScheduledExecutorService scheduledExecutorService = env.lifecycle()
                .scheduledExecutorService(nameFormat).build();
        scheduledExecutorService.scheduleWithFixedDelay(
                job,
                initialDelay.getSeconds(),
                delay.getSeconds(),
                TimeUnit.SECONDS);
    }
}
