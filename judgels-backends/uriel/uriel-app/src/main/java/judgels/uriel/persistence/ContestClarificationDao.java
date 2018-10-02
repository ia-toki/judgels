package judgels.uriel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestClarificationDao extends JudgelsDao<ContestClarificationModel> {
    Page<ContestClarificationModel> selectPagedByContestJidAndUserJid(
            String contestJid,
            String userJid,
            SelectionOptions options);

    Page<ContestClarificationModel> selectPagedByContestJid(String contestJid, SelectionOptions options);

    Optional<ContestClarificationModel> selectByContestJidAndClarificationJid(
            String contestJid,
            String clarificationJid);

    long selectCountAnsweredByContestJidAndUserJid(String contestJid, String userJid);
    long selectCountAskedByContestJid(String contestJid);
}
