package judgels.gabriel.grading;

import io.dropwizard.lifecycle.Managed;
import jakarta.inject.Provider;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.messaging.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingRequestPoller implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingRequestPoller.class);

    private final MessageListener messageListener;
    private final ThreadPoolExecutor executorService;
    private final String queueName;
    private final Provider<GradingWorker> workerFactory;

    public GradingRequestPoller(
            MessageListener messageListener,
            ThreadPoolExecutor executorService,
            String queueName,
            Provider<GradingWorker> workerFactory) {

        this.messageListener = messageListener;
        this.executorService = executorService;
        this.queueName = queueName;
        this.workerFactory = workerFactory;
    }

    @Override
    public void start() throws Exception {
        try {
            messageListener.start(queueName, executorService, message -> {
                GradingWorker worker = workerFactory.get();
                worker.process(message);
            });
        } catch (Exception e) {
            LOGGER.error("Failed to run grading request poller", e);
            throw e;
        }
    }

    @Override
    public void stop() {
        messageListener.stop();
    }
}
