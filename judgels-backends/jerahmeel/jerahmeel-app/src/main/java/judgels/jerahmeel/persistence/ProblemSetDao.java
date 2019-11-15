package judgels.jerahmeel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    Page<ProblemSetModel> selectPaged(SearchOptions searchOptions, SelectionOptions options);
}
