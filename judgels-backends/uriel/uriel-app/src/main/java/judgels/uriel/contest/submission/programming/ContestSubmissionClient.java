package judgels.uriel.contest.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.submission.programming.AbstractSubmissionClient;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.uriel.persistence.ContestProgrammingGradingModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;

public class ContestSubmissionClient extends AbstractSubmissionClient<
        ContestProgrammingSubmissionModel, ContestProgrammingGradingModel> {

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
