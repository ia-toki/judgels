package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ProblemDao extends JudgelsDao<ProblemModel> {
    Optional<ProblemModel> selectBySlug(String slug);
    Page<ProblemModel> selectPaged(String termFilter, List<Set<String>> tagsFilter, SelectionOptions options);
    Page<ProblemModel> selectPagedByUserJid(String userJid, String termFilter, List<Set<String>> tagsFilter, SelectionOptions options);
}
