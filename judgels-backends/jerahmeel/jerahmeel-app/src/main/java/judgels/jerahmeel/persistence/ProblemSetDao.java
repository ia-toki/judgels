package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    Optional<ProblemSetModel> selectBySlug(String problemSetSlug);
    Page<ProblemSetModel> selectPaged(
            Optional<String> archiveJid,
            SearchOptions searchOptions,
            SelectionOptions options);
}
