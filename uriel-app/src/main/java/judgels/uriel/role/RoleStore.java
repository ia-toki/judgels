package judgels.uriel.role;

import javax.inject.Inject;
import judgels.persistence.UnmodifiableDao;
import judgels.uriel.contest.contestant.ContestContestantDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.AdminRoleModel_;

public class RoleStore {
    private final UnmodifiableDao<AdminRoleModel> adminRoleDao;
    private final ContestContestantDao contestantDao;

    @Inject
    public RoleStore(UnmodifiableDao<AdminRoleModel> adminRoleDao, ContestContestantDao contestantDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestantDao = contestantDao;
    }

    public void addAdmin(String userJid) {
        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
    }

    public boolean isAdmin(String userJid) {
        return adminRoleDao.selectByUniqueColumn(AdminRoleModel_.userJid, userJid).isPresent();
    }

    public boolean isContestant(String contestJid, String userJid) {
        return contestantDao.existsByContestJidAndUserJid(contestJid, userJid);
    }
}
