package judgels.uriel.sandalphon;

import java.util.Map;
import java.util.Set;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.service.api.client.BasicAuthHeader;

public class ClientProblemResource implements ClientProblemService {
    @Override
    public ProblemStatement getProblemStatement(BasicAuthHeader authHeader, String problemJid) {
        return null;
    }

    @Override
    public Map<String, ProblemInfo> findProblemsByJids(BasicAuthHeader authHeader, Set<String> jids) {
        return null;
    }
}
