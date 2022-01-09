package judgels.uriel.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.SearchOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestDao extends JudgelsDao<ContestModel> {
    Optional<ContestModel> selectBySlug(String contestSlug);

    List<ContestModel> selectAllActive(SelectionOptions options);
    List<ContestModel> selectAllRunning(SelectionOptions options);

    Page<ContestModel> selectPaged(SearchOptions searchOptions, SelectionOptions options);
    Page<ContestModel> selectPagedByUserJid(String userJid, SearchOptions searchOptions, SelectionOptions options);
    List<ContestModel> selectAllActiveByUserJid(String userJid, SelectionOptions options);
    List<ContestModel> selectAllPubliclyParticipatedByUserJid(String userJid, SelectionOptions options);
    List<ContestModel> selectAllPublicAfter(Instant time);
}
