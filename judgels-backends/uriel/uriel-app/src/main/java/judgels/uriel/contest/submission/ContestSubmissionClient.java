package judgels.uriel.contest.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.submission.AbstractSubmissionClient;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestSubmissionModel;

public class ContestSubmissionClient extends AbstractSubmissionClient<ContestSubmissionModel, ContestGradingModel> {
    @Inject
    public ContestSubmissionClient(
            ContestSubmissionStore submissionStore,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            @Named("gabrielClientJid") String gabrielClientJid,
            ObjectMapper mapper) {

        super(
                submissionStore,
                sealtielClientAuthHeader,
                messageService,
                gabrielClientJid,
                mapper);
    }
}
