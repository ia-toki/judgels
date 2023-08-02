package judgels.jophiel.user.role;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.api.user.role.UserWithRole;
import judgels.jophiel.persistence.UserRoleDao;
import judgels.jophiel.persistence.UserRoleModel;
import judgels.jophiel.role.SuperadminRoleStore;

public class UserRoleStore {
    private final UserRoleDao userRoleDao;
    private final SuperadminRoleStore superadminRoleStore;

    @Inject
    public UserRoleStore(UserRoleDao adminRoleDao, SuperadminRoleStore superadminRoleStore) {
        this.userRoleDao = adminRoleDao;
        this.superadminRoleStore = superadminRoleStore;
    }

    public List<UserWithRole> getRoles() {
        List<UserRoleModel> models = userRoleDao.select().all();
        return Lists.transform(models, m -> new UserWithRole.Builder()
                .userJid(m.userJid)
                .role(fromModel(m))
                .build());
    }

    public void setRoles(Map<String, UserRole> userRolesMap) {
        for (UserRoleModel model : userRoleDao.select().all()) {
            if (!userRolesMap.containsKey(model.userJid)) {
                userRoleDao.delete(model);
            } else {
                UserRole role = userRolesMap.get(model.userJid);
                if (role.getJophiel().isEmpty()
                        && role.getSandalphon().isEmpty()
                        && role.getUriel().isEmpty()
                        && role.getJerahmeel().isEmpty()) {
                    userRoleDao.delete(model);
                }
            }
        }

        for (var entry : userRolesMap.entrySet()) {
            upsertRole(entry.getKey(), entry.getValue());
        }
    }

    public void upsertRole(String userJid, UserRole role) {
        Optional<UserRoleModel> maybeModel = userRoleDao.selectByUserJid(userJid);
        if (maybeModel.isPresent()) {
            UserRoleModel model = maybeModel.get();
            toModel(role, model);
            userRoleDao.update(model);
        } else {
            UserRoleModel model = new UserRoleModel();
            model.userJid = userJid;
            toModel(role, model);
            userRoleDao.insert(model);
        }
    }

    public UserRole getRole(String userJid) {
        UserRole.Builder role = new UserRole.Builder();

        if (superadminRoleStore.isSuperadmin(userJid)) {
            role.jophiel(JophielRole.SUPERADMIN.name());
            role.sandalphon("ADMIN");
            role.uriel("ADMIN");
            role.jerahmeel("ADMIN");
        } else {
            Optional<UserRoleModel> maybeModel = userRoleDao.selectByUserJid(userJid);
            if (maybeModel.isPresent()) {
                return fromModel(maybeModel.get());
            }
        }

        return role.build();
    }

    private UserRole fromModel(UserRoleModel model) {
        UserRole.Builder role = new UserRole.Builder();
        if (model.jophiel != null) {
            role.jophiel(model.jophiel);
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
        return role.build();
    }

    private void toModel(UserRole role, UserRoleModel model) {
        model.jophiel = role.getJophiel().orElse(null);
        model.sandalphon = role.getSandalphon().orElse(null);
        model.uriel = role.getUriel().orElse(null);
        model.jerahmeel = role.getJerahmeel().orElse(null);
    }
}
