package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.jophiel.AbstractJophielClientController;
import org.iatoki.judgels.jophiel.JophielSessionUtils;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.template.sidebar.html.profileView;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public abstract class AbstractSandalphonController extends AbstractJophielClientController {
    protected Result renderTemplate(HtmlTemplate template) {
        Http.Request req = template.getRequest();
        String userJid = JophielSessionUtils.getUserJid(req);
        String username = JophielSessionUtils.getUsername(req);

        template.addSidebarMenu("Problems", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.addSidebarMenu("Lessons", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.index());

        template.addUpperSidebarWidget(profileView.render(
                getUserAvatarUrl(userJid),
                username,
                org.iatoki.judgels.jophiel.routes.JophielClientController.logout(routes.ApplicationController.index().absoluteURL(req, req.secure())).absoluteURL(req, req.secure())
        ));
        if (userJid != null) {
            template.addAdditionalScript(isLoggedOut.render(getUserIsLoggedInAPIEndpoint(), org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(Http.Context.current().request())).absoluteURL(req, req.secure()), "javascripts/isLoggedOut.js", userJid));
        }

        return super.renderTemplate(template);
    }
}
