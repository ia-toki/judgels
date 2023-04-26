package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.CriteriaPredicate;
import judgels.persistence.JudgelsDao;

public interface ProblemDao extends JudgelsDao<ProblemModel> {
    Optional<ProblemModel> selectBySlug(String slug);

    CriteriaPredicate<ProblemModel> userCanView(String userJid, boolean isAdmin);
    CriteriaPredicate<ProblemModel> termsMatch(String term);
    CriteriaPredicate<ProblemModel> tagsMatch(List<Set<String>> tagGroups);
}
