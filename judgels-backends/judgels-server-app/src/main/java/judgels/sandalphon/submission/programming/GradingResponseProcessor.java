package judgels.sandalphon.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.util.Optional;
import judgels.gabriel.api.GradingResponse;
import judgels.messaging.api.Message;
import judgels.sandalphon.api.submission.programming.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradingResponseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradingResponseProcessor.class);

    private final ObjectMapper mapper;
    private final SubmissionStore submissionStore;
    private final SubmissionConsumer submissionConsumer;

    public GradingResponseProcessor(
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            SubmissionConsumer submissionConsumer) {

        this.mapper = mapper;
        this.submissionStore = submissionStore;
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

        Optional<Submission> submission = submissionStore.updateGrading(response.getGradingJid(), response.getResult());
        if (submission.isEmpty()) {
            LOGGER.error("Failed to find grading jid {}", response.getGradingJid());
            return;
        }

        submissionConsumer.accept(submission.get());
    }
}
