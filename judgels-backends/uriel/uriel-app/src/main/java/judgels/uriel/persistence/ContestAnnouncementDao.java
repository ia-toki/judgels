package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    Page<ContestAnnouncementModel> selectPagedPublishedByContestJid(String contestJid, SelectionOptions options);
    Page<ContestAnnouncementModel> selectPagedByContestJid(String contestJid, SelectionOptions options);
    long selectCountPublishedByContestJid(String contestJid);
}
