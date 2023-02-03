package judgels.jophiel.client.user;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;

public class ClientUserResource implements ClientUserService {
    private final UserRoleStore userRoleStore;

    @Inject
    public ClientUserResource(UserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserRole getUserRole(String userJid) {
        if (userJid.equals("guest")) {
            return new UserRole.Builder().jophiel(JophielRole.GUEST).build();
        }
        return userRoleStore.getRole(userJid);
    }
}
