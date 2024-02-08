package judgels.sandalphon.submission.programming;

import static judgels.sandalphon.submission.programming.SubmissionUtils.checkAllSourceFilesPresent;
import static judgels.sandalphon.submission.programming.SubmissionUtils.checkGradingLanguageAllowed;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.gabriel.api.GradingOptions;
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

    public Submission submit(SubmissionData data, SubmissionSource source, ProblemSubmissionConfig config) {
        return submit(data, source, config, new GradingOptions.Builder().build());
    }

    public Submission submit(SubmissionData data, SubmissionSource source, ProblemSubmissionConfig config, GradingOptions options) {
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

        Submission submission = submissionStore.createSubmission(data, config);
        String gradingJid = submissionStore.createGrading(submission);
        requestGrading(gradingJid, submission, source, config, options);

        return submission;
    }

    public void regrade(String gradingJid, Submission submission, SubmissionSource source, ProblemSubmissionConfig config) {
        regrade(gradingJid, submission, source, config, new GradingOptions.Builder().build());
    }

    public void regrade(String gradingJid, Submission submission, SubmissionSource source, ProblemSubmissionConfig config, GradingOptions options) {
        if (config.getGradingLastUpdateTime().isAfter(submission.getTime())) {
            // TODO(fushar): update submission's grading engine
        }
        requestGrading(gradingJid, submission, source, config, options);
    }

    public void requestGrading(String gradingJid, Submission submission, SubmissionSource source, ProblemSubmissionConfig config, GradingOptions options) {
        GradingRequest gradingRequest = new GradingRequest.Builder()
                .gradingJid(gradingJid)
                .problemJid(submission.getProblemJid())
                .gradingLanguage(submission.getGradingLanguage())
                .submissionSource(source)
                .gradingLastUpdateTime(config.getGradingLastUpdateTime())
                .gradingOptions(options)
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
