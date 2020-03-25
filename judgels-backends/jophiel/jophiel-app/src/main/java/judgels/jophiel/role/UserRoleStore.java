package judgels.jophiel.role;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.persistence.UserRoleDao;
import judgels.jophiel.persistence.UserRoleModel;

public class UserRoleStore {
    private final UserRoleDao userRoleDao;
    private final SuperadminRoleStore superadminRoleStore;

    @Inject
    public UserRoleStore(UserRoleDao adminRoleDao, SuperadminRoleStore superadminRoleStore) {
        this.userRoleDao = adminRoleDao;
        this.superadminRoleStore = superadminRoleStore;
    }

    public void upsertRole(String userJid, UserRole role) {
        Optional<UserRoleModel> maybeModel = userRoleDao.selectByUserJid(userJid);
        if (maybeModel.isPresent()) {
            UserRoleModel model = maybeModel.get();
            model.jophiel = role.getJophiel().name();
            model.sandalphon = role.getSandalphon().orElse(null);
            model.uriel = role.getUriel().orElse(null);
            model.jerahmeel = role.getJerahmeel().orElse(null);
            userRoleDao.update(model);
        } else {
            UserRoleModel model = new UserRoleModel();
            model.userJid = userJid;
            model.jophiel = role.getJophiel().name();
            model.sandalphon = role.getSandalphon().orElse(null);
            model.uriel = role.getUriel().orElse(null);
            model.jerahmeel = role.getJerahmeel().orElse(null);
            userRoleDao.insert(model);
        }
    }

    public UserRole getRole(String userJid) {
        UserRole.Builder role = new UserRole.Builder();

        if (superadminRoleStore.isSuperadmin(userJid)) {
            role.jophiel(JophielRole.SUPERADMIN);
            role.sandalphon("ADMIN");
            role.uriel("ADMIN");
            role.jerahmeel("ADMIN");
        } else {
            Optional<UserRoleModel> maybeModel = userRoleDao.selectByUserJid(userJid);
            if (maybeModel.isPresent()) {
                UserRoleModel model = maybeModel.get();
                if (model.jophiel != null) {
                    role.jophiel(JophielRole.valueOf(model.jophiel));
                }
                if (model.sandalphon != null) {
                    role.sandalphon(model.sandalphon);
                }
                if (model.uriel != null) {
                    role.uriel(model.uriel);
                }
                if (model.jerahmeel != null) {
                    role.jerahmeel(model.jerahmeel);
                }
            }
        }

        return role.build();
    }
}
