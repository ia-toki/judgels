package judgels.sandalphon.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.time.Duration;
import judgels.gabriel.api.GradingResponse;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponseProcessor.class);

    private static final int MAX_RETRIES = 3;
    private static final Duration DELAY_BETWEEN_RETRIES = Duration.ofSeconds(5);

    private final ObjectMapper mapper;
    private final SubmissionStore submissionStore;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;

    public GradingResponseProcessor(
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService) {

        this.mapper = mapper;
        this.submissionStore = submissionStore;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
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
            if (submissionStore.updateGrading(response.getGradingJid(), response.getResult())) {
                gradingExists = true;
                messageService.confirmMessage(sealtielClientAuthHeader, message.getId());
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
