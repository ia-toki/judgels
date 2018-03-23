package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.MyService;
import judgels.jophiel.api.user.PasswordUpdateData;
import judgels.jophiel.api.user.User;
import judgels.jophiel.role.RoleStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class MyResource implements MyService {
    private final ActorChecker actorChecker;
    private final RoleStore roleStore;
    private final UserStore userStore;

    @Inject
    public MyResource(
            ActorChecker actorChecker,
            RoleStore roleStore,
            UserStore userStore) {

        this.actorChecker = actorChecker;
        this.roleStore = roleStore;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getMyself(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);

        return checkFound(userStore.findUserByJid(actorJid));
    }

    @Override
    @UnitOfWork
    public void updateMyPassword(AuthHeader authHeader, PasswordUpdateData passwordUpdateData) {
        String actorJid = actorChecker.check(authHeader);

        userStore.validateUserPassword(actorJid, passwordUpdateData.getOldPassword());
        userStore.updateUserPassword(actorJid, passwordUpdateData.getNewPassword());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Role getMyRole(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return roleStore.getUserRole(actorJid);
    }
}
