package judgels.uriel.persistence;

import java.time.Instant;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ContestDao extends JudgelsDao<ContestModel> {
    ContestQueryBuilder select();
    Optional<ContestModel> selectBySlug(String contestSlug);

    interface ContestQueryBuilder extends QueryBuilder<ContestModel> {
        ContestQueryBuilder wherePublic();
        ContestQueryBuilder whereActive();
        ContestQueryBuilder whereRunning();
        ContestQueryBuilder whereBeginsAfter(Instant time);
        ContestQueryBuilder whereEnded();
        ContestQueryBuilder whereUserCanView(String userJid);
        ContestQueryBuilder whereUserParticipated(String userJid);
        ContestQueryBuilder whereNameLike(String name);
    }
}
