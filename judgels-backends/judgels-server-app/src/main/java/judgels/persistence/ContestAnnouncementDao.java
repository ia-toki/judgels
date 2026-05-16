package judgels.persistence;

import java.io.PrintWriter;

public interface ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    ContestAnnouncementQueryBuilder selectByContestJid(String contestJid);
    void dump(PrintWriter output, String contestJid);

    interface ContestAnnouncementQueryBuilder extends QueryBuilder<ContestAnnouncementModel> {
        ContestAnnouncementQueryBuilder whereStatusIs(String status);
    }
}
