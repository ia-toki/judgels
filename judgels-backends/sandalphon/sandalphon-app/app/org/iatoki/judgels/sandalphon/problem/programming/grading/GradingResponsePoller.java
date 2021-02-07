package org.iatoki.judgels.sandalphon.problem.programming.grading;

import akka.actor.Scheduler;
import java.time.Duration;
import java.util.Optional;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContext;

public final class GradingResponsePoller implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponsePoller.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(2);
    private static final Duration SCHEDULE_DELAY = Duration.ofMillis(10);

    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final Scheduler scheduler;
    private final ExecutionContext executor;
    private final GradingResponseProcessor processor;

    public GradingResponsePoller(
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            Scheduler scheduler,
            ExecutionContext executor,
            GradingResponseProcessor processor) {

        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.scheduler = scheduler;
        this.executor = executor;
        this.processor = processor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Optional<Message> maybeMessage = messageService.receiveMessage(sealtielClientAuthHeader);
                if (!maybeMessage.isPresent()) {
                    try {
                        Thread.sleep(POLLING_DELAY.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                Message message = maybeMessage.get();
                scheduler.scheduleOnce(SCHEDULE_DELAY, () -> processor.process(message), executor);
            } catch (Throwable e) {
                LOGGER.error("Failed to run grading response poller", e);
                try {
                    Thread.sleep(POLLING_DELAY.toMillis());
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
