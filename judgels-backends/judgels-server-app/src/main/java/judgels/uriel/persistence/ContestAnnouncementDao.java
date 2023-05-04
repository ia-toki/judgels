package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    ContestAnnouncementQueryBuilder selectByContestJid(String contestJid);

    interface ContestAnnouncementQueryBuilder extends QueryBuilder<ContestAnnouncementModel> {
        ContestAnnouncementQueryBuilder whereStatusIs(String status);
    }
}
