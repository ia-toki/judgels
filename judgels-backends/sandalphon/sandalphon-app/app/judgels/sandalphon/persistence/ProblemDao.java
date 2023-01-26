package judgels.sandalphon.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;

public interface ProblemDao extends JudgelsDao<ProblemModel> {

    List<String> getJidsByAuthorJid(String authorJid);

    ProblemModel findBySlug(String slug);

    boolean existsBySlug(String slug);
}
