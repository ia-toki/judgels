package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedIn;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.AbstractJudgelsController;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.template.sidebar.html.guestView;
import org.iatoki.judgels.play.template.sidebar.html.profileView;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

public class AbstractJerahmeelController extends AbstractJudgelsController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.addSidebarMenu(Messages.get("training.training"), org.iatoki.judgels.jerahmeel.training.routes.TrainingController.index());
        template.addSidebarMenu(Messages.get("submission.submissions"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToSubmissions());
        // template.addSidebarMenu(Messages.get("statistic.statistics"), org.iatoki.judgels.jerahmeel.statistic.routes.StatisticController.index()));
        if (isAdmin()) {
            template.addSidebarMenu(Messages.get("curriculum.curriculums"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.viewCurriculums());
            template.addSidebarMenu(Messages.get("course.courses"), org.iatoki.judgels.jerahmeel.course.routes.CourseController.viewCourses());
            template.addSidebarMenu(Messages.get("chapter.chapters"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.viewChapters());
            template.addSidebarMenu(Messages.get("user.users"), org.iatoki.judgels.jerahmeel.user.routes.UserController.index());
        }
        if (JerahmeelUtils.isGuest()) {
            template.addUpperSidebarWidget(guestView.render(routes.ApplicationController.auth(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), JophielClientControllerUtils.getInstance().getRegisterUrl().toString()));
        } else {
            template.addUpperSidebarWidget(profileView.render(
                    JophielClientControllerUtils.getInstance().getUserAvatarUrl(IdentityUtils.getUserJid()),
                    IdentityUtils.getUsername(),
                    IdentityUtils.getUserRealName(),
                    org.iatoki.judgels.jophiel.routes.JophielClientController.profile().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()),
                    org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())
            ));
        }

        if (JerahmeelUtils.isGuest()) {
            template.addAdditionalScript(isLoggedIn.render(JophielClientControllerUtils.getInstance().getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.auth(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedIn.js"));
        } else {
            template.addAdditionalScript(isLoggedOut.render(JophielClientControllerUtils.getInstance().getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.logout(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedOut.js", JerahmeelUtils.getRealUserJid()));
        }

        return super.renderTemplate(template);
    }

    protected boolean isAdmin() {
        return JerahmeelUtils.hasRole("admin");
    }
}
