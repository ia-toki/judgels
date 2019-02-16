package judgels.sandalphon.submission;

import static judgels.sandalphon.SandalphonUtils.checkAllSourceFilesPresent;
import static judgels.sandalphon.SandalphonUtils.checkGradingLanguageAllowed;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import judgels.gabriel.api.GradingRequest;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmission;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmissionData;
import judgels.sandalphon.persistence.AbstractGradingModel;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;

public abstract class AbstractSubmissionClient<SM extends AbstractSubmissionModel, GM extends AbstractGradingModel>  {
    private final AbstractSubmissionStore<SM, GM> submissionStore;
    private final BasicAuthHeader sealtielClientAuthHeader;
    private final MessageService messageService;
    private final String gabrielClientJid;
    private final ObjectMapper mapper;

    public AbstractSubmissionClient(
            AbstractSubmissionStore<SM, GM> submissionStore,
            BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            String gabrielClientJid,
            ObjectMapper mapper) {

        this.submissionStore = submissionStore;
        this.sealtielClientAuthHeader = sealtielClientAuthHeader;
        this.messageService = messageService;
        this.gabrielClientJid = gabrielClientJid;
        this.mapper = mapper;
    }

    public ProgrammingSubmission submit(
            ProgrammingSubmissionData data,
            SubmissionSource source,
            ProblemSubmissionConfig config) {
        checkAllSourceFilesPresent(source, config);
        checkGradingLanguageAllowed(
                data.getGradingLanguage(),
                config.getGradingLanguageRestriction(),
                data.getAdditionalGradingLanguageRestriction());

        ProgrammingSubmission submission = submissionStore.createSubmission(data, config.getGradingEngine());
        requestGrading(submission, source);

        return submission;
    }

    private void requestGrading(ProgrammingSubmission submission, SubmissionSource source) {
        String gradingJid = submissionStore.createGrading(submission);
        GradingRequest gradingRequest = new GradingRequest.Builder()
                .gradingJid(gradingJid)
                .problemJid(submission.getProblemJid())
                .gradingEngine(submission.getGradingEngine())
                .gradingLanguage(submission.getGradingLanguage())
                .submissionSource(source)
                .build();

        MessageData data;
        try {
            data = new MessageData.Builder()
                    .targetJid(gabrielClientJid)
                    .type(GradingRequest.class.getSimpleName())
                    .content(mapper.writeValueAsString(gradingRequest))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        messageService.sendMessage(sealtielClientAuthHeader, data);
    }
}
