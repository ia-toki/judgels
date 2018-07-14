package judgels.uriel.contest.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import judgels.sandalphon.submission.AbstractSubmissionClient;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.gabriel.GabrielClientJid;
import judgels.uriel.persistence.ContestGradingModel;
import judgels.uriel.persistence.ContestSubmissionModel;
import judgels.uriel.sealtiel.SealtielClientAuthHeader;

public class ContestSubmissionClient extends AbstractSubmissionClient<ContestSubmissionModel, ContestGradingModel> {
    @Inject
    public ContestSubmissionClient(
            ContestSubmissionStore submissionStore,
            @SealtielClientAuthHeader BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            @GabrielClientJid String gabrielClientJid,
            ObjectMapper mapper) {

        super(
                submissionStore,
                sealtielClientAuthHeader,
                messageService,
                gabrielClientJid,
                mapper);
    }
}
