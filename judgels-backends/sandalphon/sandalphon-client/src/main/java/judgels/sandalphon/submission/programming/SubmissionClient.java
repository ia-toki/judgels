package judgels.sandalphon.submission.programming;

import static judgels.sandalphon.submission.programming.SubmissionUtils.checkAllSourceFilesPresent;
import static judgels.sandalphon.submission.programming.SubmissionUtils.checkGradingLanguageAllowed;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.gabriel.api.GradingRequest;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.api.SubmissionSource;
import judgels.messaging.MessageClient;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.api.submission.programming.SubmissionData;

public class SubmissionClient {
    private final SubmissionStore submissionStore;
    private final String gradingRequestQueueName;
    private final String gradingResponseQueueName;
    private final MessageClient messageClient;
    private final ObjectMapper mapper;

    public SubmissionClient(
            SubmissionStore submissionStore,
            String gradingRequestQueueName,
            String gradingResponseQueueName,
            MessageClient messageClient,
            ObjectMapper mapper) {

        this.submissionStore = submissionStore;
        this.gradingRequestQueueName = gradingRequestQueueName;
        this.gradingResponseQueueName = gradingResponseQueueName;
        this.messageClient = messageClient;
        this.mapper = mapper;
    }

    public Submission submit(
            SubmissionData data,
            SubmissionSource source,
            ProblemSubmissionConfig config) {

        LanguageRestriction restriction = config.getGradingLanguageRestriction();
        if (data.getAdditionalGradingLanguageRestriction().isPresent()) {
            restriction = LanguageRestriction.combine(
                    restriction,
                    data.getAdditionalGradingLanguageRestriction().get());
        }

        checkAllSourceFilesPresent(source, config);
        checkGradingLanguageAllowed(
                config.getGradingEngine(),
                data.getGradingLanguage(),
                restriction);

        Submission submission = submissionStore.createSubmission(data, config.getGradingEngine());
        String gradingJid = submissionStore.createGrading(submission);
        requestGrading(gradingJid, submission, source);

        return submission;
    }

    public void requestGrading(String gradingJid, Submission submission, SubmissionSource source) {
        GradingRequest gradingRequest = new GradingRequest.Builder()
                .gradingJid(gradingJid)
                .problemJid(submission.getProblemJid())
                .gradingEngine(submission.getGradingEngine())
                .gradingLanguage(submission.getGradingLanguage())
                .submissionSource(source)
                .build();

        try {
            messageClient.sendMessage(
                    gradingResponseQueueName,
                    gradingRequestQueueName,
                    GradingRequest.class.getSimpleName(),
                    mapper.writeValueAsString(gradingRequest));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
