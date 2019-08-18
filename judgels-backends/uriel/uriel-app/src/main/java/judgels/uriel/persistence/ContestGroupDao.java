package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface ContestGroupDao extends JudgelsDao<ContestGroupModel> {
    Optional<ContestGroupModel> selectBySlug(String contestGroupSlug);
}
