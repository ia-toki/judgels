package judgels.sandalphon.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface BaseSubmissionDao<M extends AbstractSubmissionModel> extends JudgelsDao<M> {
    M createSubmissionModel();
    Page<M> selectPaged(String containerJid, Optional<String> userJid, SelectionOptions options);
    Map<String, Long> selectCounts(String containerJid, String userJid, Set<String> problemJids);
}
