package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;

public interface ContestRoleDao extends JudgelsDao<ContestModel> {
    boolean isContestantOrAbove(String userJid, String contestJid);
    boolean isSupervisorOrAbove(String userJid, String contestJid);
    boolean isManager(String userJid, String contestJid);
}
