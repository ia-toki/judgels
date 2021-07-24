package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import judgels.gabriel.api.GradingResponse;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.db.jpa.JPAApi;

public final class GradingResponseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponseProcessor.class);

    private static final int MAX_RETRIES = 3;
    private static final Duration DELAY_BETWEEN_RETRIES = Duration.ofSeconds(5);

    private final JPAApi jpaApi;
    private final ObjectMapper mapper;
    private final SubmissionStore submissionStore;
    private final MessageClient messageClient;

    public GradingResponseProcessor(
            JPAApi jpaApi,
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            MessageClient messageClient) {

        this.jpaApi = jpaApi;
        this.mapper = mapper;
        this.submissionStore = submissionStore;
        this.messageClient = messageClient;
    }

    public void process(Message message) {
        jpaApi.withTransaction(() -> {
            try {
                GradingResponse response = mapper.readValue(message.getContent(), GradingResponse.class);

                boolean gradingExists = false;

                // it is possible that the grading model is not immediately found, because it is not flushed yet.
                for (int i = 0; i < MAX_RETRIES; i++) {
                    Optional<Submission> submission =
                            submissionStore.updateGrading(response.getGradingJid(), response.getResult());
                    if (submission.isPresent()) {
                        gradingExists = true;
                        messageClient.confirmMessage(message.getId());
                        break;
                    }

                    try {
                        Thread.sleep(DELAY_BETWEEN_RETRIES.toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (!gradingExists) {
                    LOGGER.error("Failed to find grading jid {}", response.getGradingJid());
                }
            } catch (Throwable e) {
                LOGGER.error("Failed to process grading response", e);
            }
        });
    }
}
