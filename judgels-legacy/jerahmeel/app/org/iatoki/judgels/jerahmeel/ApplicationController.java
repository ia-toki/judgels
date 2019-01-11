package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.jerahmeel.user.User;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.user.UserService;
import org.iatoki.judgels.jerahmeel.avatar.AvatarCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class ApplicationController extends AbstractJudgelsController {
    private final UserService userService;

    @Inject
    public ApplicationController(UserService userService) {
        this.userService = userService;
    }

    public Result index() {
        return redirect(org.iatoki.judgels.jerahmeel.training.routes.TrainingController.index());
    }

    public Result auth(String returnUri) {
        if (session().containsKey("username") && session().containsKey("role")) {
            return redirect(returnUri);
        } else if (session().containsKey("username")) {
            return redirect(routes.ApplicationController.authRole(returnUri));
        } else {
            String newReturnUri = routes.ApplicationController.afterLogin(returnUri).absoluteURL(request(), request().secure());
            return redirect(org.iatoki.judgels.jophiel.routes.JophielClientController.login(newReturnUri));
        }
    }

    @Transactional
    public Result authRole(String returnUri) {
        if (session().containsKey("username") && session().containsKey("role")) {
            return redirect(returnUri);
        }

        String userRoleJid = IdentityUtils.getUserJid();
        if (!userService.existsByUserJid(userRoleJid)) {
            userService.createUser(userRoleJid, JerahmeelUtils.getDefaultRoles(), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            JerahmeelUtils.saveRolesInSession(JerahmeelUtils.getDefaultRoles());
            return redirect(returnUri);
        }

        User userRole = userService.findUserByJid(userRoleJid);
        JerahmeelUtils.saveRolesInSession(userRole.getRoles());
        return redirect(returnUri);
    }

    @Transactional
    public Result afterLogin(String returnUri) {
        if (!session().containsKey("role")) {
            String newReturnUri = routes.ApplicationController.afterLogin(returnUri).absoluteURL(request(), request().secure());
            return redirect(routes.ApplicationController.authRole(newReturnUri));
        }

        JudgelsPlayUtils.updateUserJidCache(JidCacheServiceImpl.getInstance());
        JophielClientControllerUtils.updateUserAvatarCache(AvatarCacheServiceImpl.getInstance());
        return redirect(returnUri);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    public Result logout(String returnUri) {
        session().clear();
        return redirect(returnUri);
    }
}
