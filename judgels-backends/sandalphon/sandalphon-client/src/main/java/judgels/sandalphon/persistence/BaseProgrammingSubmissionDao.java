package judgels.sandalphon.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface BaseProgrammingSubmissionDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();
    Page<M> selectPaged(
            String containerJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Long> lastSubmissionId,
            SelectionOptions options);
    Map<String, Long> selectCounts(String containerJid, String userJid, Set<String> problemJids);
}
