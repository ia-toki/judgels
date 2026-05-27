package judgels.persistence.dao;

import java.io.PrintWriter;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ContestAnnouncementModel;

public interface ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    ContestAnnouncementQueryBuilder selectByContestJid(String contestJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestAnnouncementQueryBuilder extends QueryBuilder<ContestAnnouncementModel> {
        ContestAnnouncementQueryBuilder whereStatusIs(String status);
    }
}
