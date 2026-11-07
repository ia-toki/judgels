package judgels.user.role;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserWithRoles;
import judgels.persistence.dao.UserRoleDao;
import judgels.persistence.model.UserRoleModel;

public class UserRoleStore {
    private final UserRoleDao userRoleDao;

    @Inject
    public UserRoleStore(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    public Set<UserRole> getRoles(String userJid) {
        return expand(fromModels(userRoleDao.selectAllByUserJid(userJid)));
    }

    public List<String> getUserJidsByRole(UserRole role) {
        return userRoleDao.selectAllByRole(role).stream()
                .map(m -> m.userJid)
                .collect(Collectors.toList());
    }

    public List<UserWithRoles> getUsersWithRoles() {
        Map<String, Set<UserRole>> rolesByUserJid = new LinkedHashMap<>();
        for (UserRoleModel m : userRoleDao.selectAll()) {
            rolesByUserJid.computeIfAbsent(m.userJid, k -> EnumSet.noneOf(UserRole.class)).add(UserRole.valueOf(m.role));
        }

        List<UserWithRoles> superadmins = new ArrayList<>();
        List<UserWithRoles> others = new ArrayList<>();
        for (Map.Entry<String, Set<UserRole>> e : rolesByUserJid.entrySet()) {
            Set<UserRole> roles = e.getValue();
            UserWithRoles userWithRoles = new UserWithRoles.Builder()
                    .userJid(e.getKey())
                    .roles(expand(roles))
                    .build();
            (roles.contains(UserRole.SUPERADMIN) ? superadmins : others).add(userWithRoles);
        }

        List<UserWithRoles> result = new ArrayList<>(superadmins);
        result.addAll(others);
        return result;
    }

    public void setRoles(String userJid, Set<UserRole> roles, Set<UserRole> scope) {
        Set<UserRole> held = EnumSet.noneOf(UserRole.class);
        for (UserRoleModel m : userRoleDao.selectAllByUserJid(userJid)) {
            UserRole role = UserRole.valueOf(m.role);
            if (roles.contains(role) || !scope.contains(role)) {
                held.add(role);
            } else {
                userRoleDao.delete(m);
            }
        }
        userRoleDao.flush();

        for (UserRole role : roles) {
            if (scope.contains(role) && !held.contains(role)) {
                insert(userJid, role);
            }
        }
    }

    public void addRoles(String userJid, Set<UserRole> roles) {
        Set<UserRole> held = fromModels(userRoleDao.selectAllByUserJid(userJid));
        for (UserRole role : roles) {
            if (!held.contains(role)) {
                insert(userJid, role);
            }
        }
    }

    private void insert(String userJid, UserRole role) {
        UserRoleModel m = new UserRoleModel();
        m.userJid = userJid;
        m.role = role.name();
        userRoleDao.insert(m);
    }

    private static Set<UserRole> fromModels(Collection<UserRoleModel> models) {
        Set<UserRole> roles = EnumSet.noneOf(UserRole.class);
        for (UserRoleModel m : models) {
            roles.add(UserRole.valueOf(m.role));
        }
        return roles;
    }

    private static Set<UserRole> expand(Set<UserRole> roles) {
        if (roles.contains(UserRole.SUPERADMIN)) {
            return EnumSet.allOf(UserRole.class);
        }
        return roles;
    }
}
