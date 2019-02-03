package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.jerahmeel.user.User;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.user.UserService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

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

        String userJid = IdentityUtils.getUserJid();

        List<String> roles = new ArrayList<>();
        if (!userService.existsByUserJid(userJid)) {
            roles.addAll(JerahmeelUtils.getDefaultRoles());
        } else {
            User userRole = userService.findUserByJid(userJid);
            roles.addAll(userRole.getRoles());
        }

        if (JerahmeelUtils.getRealUsername().equals("superadmin")) {
            if (!roles.contains("admin")) {
                roles.add("admin");
            }
        }

        userService.updateUser(userJid, roles, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        JerahmeelUtils.saveRolesInSession(roles);

        return redirect(returnUri);
    }

    @Transactional
    public Result afterLogin(String returnUri) {
        if (!session().containsKey("role")) {
            String newReturnUri = routes.ApplicationController.afterLogin(returnUri).absoluteURL(request(), request().secure());
            return redirect(routes.ApplicationController.authRole(newReturnUri));
        }

        JudgelsPlayUtils.updateUserJidCache(JidCacheServiceImpl.getInstance());
        return redirect(returnUri);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    public Result logout(String returnUri) {
        session().clear();
        return redirect(returnUri);
    }
}
