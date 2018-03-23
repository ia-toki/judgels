package judgels.uriel.role;

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.AdminRoleModel_;
import judgels.uriel.persistence.ContestContestantModel_;
import judgels.uriel.persistence.Daos.AdminRoleDao;
import judgels.uriel.persistence.Daos.ContestContestantDao;

public class RoleStore {
    private final AdminRoleDao adminRoleDao;
    private final ContestContestantDao contestantDao;

    @Inject
    public RoleStore(AdminRoleDao adminRoleDao, ContestContestantDao contestantDao) {
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
        return contestantDao.selectByUniqueColumns(ImmutableMap.of(
                ContestContestantModel_.contestJid, contestJid,
                ContestContestantModel_.userJid, userJid)).isPresent();
    }
}
