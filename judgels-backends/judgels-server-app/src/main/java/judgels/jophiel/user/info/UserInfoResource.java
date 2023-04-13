package judgels.jophiel.user.info;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.api.user.info.UserInfoService;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserInfoResource implements UserInfoService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;
    private final UserInfoStore infoStore;

    @Inject
    public UserInfoResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            UserInfoStore infoStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.infoStore = infoStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserInfo getInfo(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return infoStore.getInfo(user.getJid());
    }

    @Override
    @UnitOfWork
    public UserInfo updateInfo(AuthHeader authHeader, String userJid, UserInfo userInfo) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return infoStore.upsertInfo(user.getJid(), userInfo);
    }
}
