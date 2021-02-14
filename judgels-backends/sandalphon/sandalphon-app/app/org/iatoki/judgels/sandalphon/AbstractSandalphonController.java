package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.iatoki.judgels.jophiel.AbstractJophielClientController;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.template.sidebar.html.profileView;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public abstract class AbstractSandalphonController extends AbstractJophielClientController {
    protected String getCurrentStatementLanguage(Http.Request req) {
        return req.session().getOptional("currentStatementLanguage").orElse(null);
    }

    protected Map<String, String> newCurrentStatementLanguage(String value) {
        if (value == null) {
            return ImmutableMap.of();
        }
        return ImmutableMap.of("currentStatementLanguage", value);
    }

    protected Result renderTemplate(HtmlTemplate template) {
        Http.Request req = template.getRequest();
        String userJid = getUserJid(req);
        String username = getUsername(req);

        template.addSidebarMenu("Problems", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.addSidebarMenu("Lessons", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.index());

        template.addUpperSidebarWidget(profileView.render(
                getUserAvatarUrl(userJid),
                username,
                org.iatoki.judgels.jophiel.routes.JophielClientController.logout(routes.ApplicationController.index().absoluteURL(req, req.secure())).absoluteURL(req, req.secure())
        ));
        if (userJid != null) {
            template.addAdditionalScript(isLoggedOut.render(getUserIsLoggedInAPIEndpoint(), org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(req)).absoluteURL(req, req.secure()), "javascripts/isLoggedOut.js", userJid));
        }

        return super.renderTemplate(template);
    }
}
