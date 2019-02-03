package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.sandalphon.user.User;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.user.UserService;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
        if (session().containsKey("username") && session().containsKey("role")) {
            return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        } else if (session().containsKey("username")) {
            String returnUri = org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index().absoluteURL(request(), request().secure());
            return redirect(routes.ApplicationController.authRole(returnUri));
        } else {
            String returnUri = org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index().absoluteURL(request(), request().secure());
            return redirect(routes.ApplicationController.auth(returnUri));
        }
    }

    public Result auth(String returnUri) {
        if (session().containsKey("username") && session().containsKey("role")) {
            return redirect(returnUri);
        } else if (session().containsKey("username")) {
            return redirect(routes.ApplicationController.authRole(returnUri));
        } else {
            try {
                String newReturnUri = routes.ApplicationController.afterLogin(URLEncoder.encode(returnUri, "UTF-8")).absoluteURL(request(), request().secure());
                return redirect(org.iatoki.judgels.jophiel.routes.JophielClientController.login(newReturnUri));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
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
            roles.addAll(SandalphonUtils.getDefaultRoles());
        } else {
            User userRole = userService.findUserByJid(userJid);
            roles.addAll(userRole.getRoles());
        }

        if (SandalphonUtils.getRealUsername().equals("superadmin")) {
            if (!roles.contains("admin")) {
                roles.add("admin");
            }
        }

        userService.updateUser(userJid, roles, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        SandalphonUtils.saveRolesInSession(roles);

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
