package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.jophiel.AbstractJophielClientController;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.template.sidebar.html.profileView;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractSandalphonController extends AbstractJophielClientController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.addSidebarMenu(Messages.get("problem.problems"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.addSidebarMenu(Messages.get("lesson.lessons"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.index());

        template.addUpperSidebarWidget(profileView.render(
                getUserAvatarUrl(IdentityUtils.getUserJid()),
                IdentityUtils.getUsername(),
                org.iatoki.judgels.jophiel.routes.JophielClientController.logout(routes.ApplicationController.index().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())
        ));
        if (IdentityUtils.getUserJid() != null) {
            template.addAdditionalScript(isLoggedOut.render(getUserIsLoggedInAPIEndpoint(), org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedOut.js", SandalphonUtils.getRealUserJid()));
        }

        return super.renderTemplate(template);
    }
}
