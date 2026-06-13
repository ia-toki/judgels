package judgels.user.role;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.role.UserAdminRole;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserWithRole;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.dao.UserRoleDao;
import judgels.persistence.model.UserRoleModel;
import judgels.role.SuperadminRoleStore;

public class UserRoleStore {
    private final UserRoleDao userRoleDao;
    private final SuperadminRoleStore superadminRoleStore;

    @Inject
    public UserRoleStore(UserRoleDao adminRoleDao, SuperadminRoleStore superadminRoleStore) {
        this.userRoleDao = adminRoleDao;
        this.superadminRoleStore = superadminRoleStore;
    }

    public List<UserWithRole> getRoles() {
        List<UserRoleModel> models = userRoleDao.select().orderBy(UnmodifiableModel_.ID, OrderDir.ASC).all();
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
                if (role.getAccount().isEmpty()
                        && role.getProblem().isEmpty()
                        && role.getContest().isEmpty()
                        && role.getTraining().isEmpty()) {
                    userRoleDao.delete(model);
                }
            }
        }

        for (var entry : userRolesMap.entrySet()) {
            UserRole role = entry.getValue();
            if (role.getAccount().isEmpty()
                    && role.getProblem().isEmpty()
                    && role.getContest().isEmpty()
                    && role.getTraining().isEmpty()) {
                continue;
            }
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
            role.account(UserAdminRole.SUPERADMIN.name());
            role.problem("ADMIN");
            role.contest("ADMIN");
            role.training("ADMIN");
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
            role.account(model.jophiel);
        }
        if (model.sandalphon != null) {
            role.problem(model.sandalphon);
        }
        if (model.uriel != null) {
            role.contest(model.uriel);
        }
        if (model.jerahmeel != null) {
            role.training(model.jerahmeel);
        }
        return role.build();
    }

    private void toModel(UserRole role, UserRoleModel model) {
        model.jophiel = role.getAccount().orElse(null);
        model.sandalphon = role.getProblem().orElse(null);
        model.uriel = role.getContest().orElse(null);
        model.jerahmeel = role.getTraining().orElse(null);
    }
}
