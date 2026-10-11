package judgels.user.role;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import judgels.api.user.role.SystemAdminRole;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserWithRole;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.dao.UserRoleDao;
import judgels.persistence.model.UserRoleModel;

public class UserRoleStore {
    private final UserRoleDao userRoleDao;
    private final SuperadminRoleStore superadminRoleStore;

    @Inject
    public UserRoleStore(UserRoleDao adminRoleDao, SuperadminRoleStore superadminRoleStore) {
        this.userRoleDao = adminRoleDao;
        this.superadminRoleStore = superadminRoleStore;
    }

    public List<UserWithRole> getRoles() {
        List<UserWithRole> roles = new ArrayList<>();
        superadminRoleStore.getSuperadminJid().ifPresent(userJid -> roles.add(new UserWithRole.Builder()
                .userJid(userJid)
                .role(getRole(userJid))
                .build()));

        for (UserRoleModel m : userRoleDao.select().orderBy(UnmodifiableModel_.ID, OrderDir.ASC).all()) {
            if (!superadminRoleStore.isSuperadmin(m.userJid)) {
                roles.add(new UserWithRole.Builder()
                        .userJid(m.userJid)
                        .role(fromModel(m))
                        .build());
            }
        }
        return roles;
    }

    public void setRole(String userJid, UserRole role) {
        Optional<UserRoleModel> maybeModel = userRoleDao.selectByUserJid(userJid);
        if (isEmpty(role)) {
            maybeModel.ifPresent(userRoleDao::delete);
        } else if (maybeModel.isPresent()) {
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

    public void upsertRole(String userJid, UserRole role) {
        UserRole.Builder merged = new UserRole.Builder();
        userRoleDao.selectByUserJid(userJid).ifPresent(model -> merged.from(fromModel(model)));
        role.getAccount().ifPresent(merged::account);
        role.getProblem().ifPresent(merged::problem);
        role.getContest().ifPresent(merged::contest);
        role.getTraining().ifPresent(merged::training);
        setRole(userJid, merged.build());
    }

    public UserRole getRole(String userJid) {
        UserRole.Builder role = new UserRole.Builder();

        if (superadminRoleStore.isSuperadmin(userJid)) {
            role.account(SystemAdminRole.SUPERADMIN.name());
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

    private static boolean isEmpty(UserRole role) {
        return role.getAccount().isEmpty()
                && role.getProblem().isEmpty()
                && role.getContest().isEmpty()
                && role.getTraining().isEmpty();
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
