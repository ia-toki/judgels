package judgels.jerahmeel.role;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jerahmeel.api.role.JerahmeelRole;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;

@Singleton
public class RoleChecker {
    private final ClientUserService clientUserService;

    private final LoadingCache<String, UserRole> userRoleCache;

    @Inject
    public RoleChecker(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;

        this.userRoleCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build(this::getRoleUncached);
    }

    public boolean isAdmin(String userJid) {
        UserRole role = userRoleCache.get(userJid);
        return role.getJophiel() == JophielRole.SUPERADMIN
                || role.getJerahmeel().orElse("").equals(JerahmeelRole.ADMIN.name());
    }

    @VisibleForTesting
    void setRoles(Map<String, UserRole> rolesMap) {
        userRoleCache.putAll(rolesMap);
    }

    private UserRole getRoleUncached(String userJid) {
        return clientUserService.getUserRole(userJid);
    }
}
