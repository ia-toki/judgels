package judgels.sandalphon.submission.programming;

import io.dropwizard.lifecycle.Managed;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.messaging.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponsePoller implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponsePoller.class);

    private final MessageListener messageListener;
    private final String queueName;
    private final ThreadPoolExecutor executorService;
    private final GradingResponseProcessor processor;

    public GradingResponsePoller(
            MessageListener messageListener,
            String queueName,
            ThreadPoolExecutor executorService,
            GradingResponseProcessor processor) {

        this.messageListener = messageListener;
        this.queueName = queueName;
        this.executorService = executorService;
        this.processor = processor;
    }

    @Override
    public void start() throws Exception {
        try {
            messageListener.start(queueName, executorService, message -> {
                processor.process(message);
            });
        } catch (Exception e) {
            LOGGER.error("Failed to run grading response poller", e);
            throw e;
        }
    }

    @Override
    public void stop() {
        messageListener.stop();
    }
}
