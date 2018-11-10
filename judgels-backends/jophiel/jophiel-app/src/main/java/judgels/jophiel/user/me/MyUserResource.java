package judgels.jophiel.user.me;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.jophiel.api.user.me.PasswordUpdateData;
import judgels.jophiel.role.RoleStore;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class MyUserResource implements MyUserService {
    private final ActorChecker actorChecker;
    private final RoleStore roleStore;
    private final UserStore userStore;

    @Inject
    public MyUserResource(ActorChecker actorChecker, RoleStore roleStore, UserStore userStore) {
        this.actorChecker = actorChecker;
        this.roleStore = roleStore;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getMyself(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return checkFound(userStore.getUserByJid(actorJid));
    }

    @Override
    @UnitOfWork
    public void updateMyPassword(AuthHeader authHeader, PasswordUpdateData data) {
        String actorJid = actorChecker.check(authHeader);

        userStore.validateUserPassword(actorJid, data.getOldPassword());
        userStore.updateUserPassword(actorJid, data.getNewPassword());
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Role getMyRole(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return roleStore.getUserRole(actorJid);
    }
}
