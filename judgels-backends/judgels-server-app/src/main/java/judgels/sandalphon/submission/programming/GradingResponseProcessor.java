package judgels.sandalphon.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import judgels.gabriel.api.GradingResponse;
import judgels.messaging.MessageClient;
import judgels.messaging.api.Message;
import judgels.sandalphon.api.submission.programming.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponseProcessor.class);

    private static final int MAX_RETRIES = 3;
    private static final Duration DELAY_BETWEEN_RETRIES = Duration.ofSeconds(5);

    private final ObjectMapper mapper;
    private final SubmissionStore submissionStore;
    private final MessageClient messageClient;
    private final SubmissionConsumer submissionConsumer;

    public GradingResponseProcessor(
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            MessageClient messageClient,
            SubmissionConsumer submissionConsumer) {

        this.mapper = mapper;
        this.submissionStore = submissionStore;
        this.messageClient = messageClient;
        this.submissionConsumer = submissionConsumer;
    }

    @UnitOfWork
    public void process(Message message) {
        GradingResponse response;
        try {
            response = mapper.readValue(message.getContent(), GradingResponse.class);
        } catch (IOException e) {
            LOGGER.error("Failed to parse grading response: {}", message.getContent());
            return;
        }

        boolean gradingExists = false;

        // it is possible that the grading model is not immediately found, because it is not flushed yet.
        for (int i = 0; i < MAX_RETRIES; i++) {
            Optional<Submission> submission =
                    submissionStore.updateGrading(response.getGradingJid(), response.getResult());
            if (submission.isPresent()) {
                gradingExists = true;
                submissionConsumer.accept(submission.get());
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
    }
}
