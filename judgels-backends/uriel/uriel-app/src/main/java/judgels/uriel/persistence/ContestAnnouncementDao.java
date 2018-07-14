package judgels.uriel.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;

public interface ContestAnnouncementDao extends JudgelsDao<ContestAnnouncementModel> {
    List<ContestAnnouncementModel> selectAllPublishedByContestJid(String contestJid);
    long selectCountPublishedByContestJid(String contestJid);
}
