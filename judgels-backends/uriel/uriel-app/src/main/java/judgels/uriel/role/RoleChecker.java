package judgels.uriel.role;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.uriel.api.role.UrielRole;

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
                || role.getUriel().orElse("").equals(UrielRole.ADMIN.name());
    }

    private UserRole getRoleUncached(String userJid) {
        return clientUserService.getUserRole(userJid);
    }
}
