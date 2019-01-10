package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.jophiel.activity.ActivityKey;
import org.iatoki.judgels.jophiel.activity.UserActivityMessage;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedInLayout;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOutLayout;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsControllerUtils;
import org.iatoki.judgels.play.controllers.ControllerUtils;
import org.iatoki.judgels.play.views.html.layouts.contentLayout;
import org.iatoki.judgels.play.views.html.layouts.menusLayout;
import org.iatoki.judgels.play.views.html.layouts.profileView;
import org.iatoki.judgels.play.views.html.layouts.sidebarLayout;
import org.iatoki.judgels.sandalphon.activity.ActivityLogServiceImpl;
import play.i18n.Messages;
import play.mvc.Http;

public final class SandalphonControllerUtils extends AbstractJudgelsControllerUtils {

    private static SandalphonControllerUtils INSTANCE;

    private final JophielClientAPI jophielClientAPI;
    private final JophielPublicAPI jophielPublicAPI;

    public SandalphonControllerUtils(JophielClientAPI jophielClientAPI, JophielPublicAPI jophielPublicAPI) {
        this.jophielClientAPI = jophielClientAPI;
        this.jophielPublicAPI = jophielPublicAPI;
    }

    @Override
    public void appendSidebarLayout(LazyHtml content) {
        content.appendLayout(c -> contentLayout.render(c));

        ImmutableList.Builder<InternalLink> internalLinkBuilder = ImmutableList.builder();

        internalLinkBuilder.add(new InternalLink(Messages.get("problem.problems"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index()));
        internalLinkBuilder.add(new InternalLink(Messages.get("lesson.lessons"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.index()));
        if (isAdmin()) {
            internalLinkBuilder.add(new InternalLink(Messages.get("client.clients"), org.iatoki.judgels.sandalphon.client.routes.ClientController.index()));
            internalLinkBuilder.add(new InternalLink(Messages.get("grader.graders"), org.iatoki.judgels.sandalphon.grader.routes.GraderController.index()));
            internalLinkBuilder.add(new InternalLink(Messages.get("user.users"), org.iatoki.judgels.sandalphon.user.routes.UserController.index()));
        }

        LazyHtml sidebarContent = new LazyHtml(profileView.render(
                IdentityUtils.getUsername(),
                IdentityUtils.getUserRealName(),
                org.iatoki.judgels.jophiel.routes.JophielClientController.profile().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()),
                org.iatoki.judgels.jophiel.routes.JophielClientController.logout(routes.ApplicationController.index().absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure())
        ));
        sidebarContent.appendLayout(c -> menusLayout.render(internalLinkBuilder.build(), c));

        content.appendLayout(c -> sidebarLayout.render(sidebarContent.render(), c));
        if (IdentityUtils.getUserJid() == null) {
            content.appendLayout(c -> isLoggedInLayout.render(jophielClientAPI.getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.auth(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedIn.js", c));
        } else {
            content.appendLayout(c -> isLoggedOutLayout.render(jophielClientAPI.getUserIsLoggedInAPIEndpoint(), routes.ApplicationController.logout(ControllerUtils.getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedOut.js", SandalphonUtils.getRealUserJid(), c));
        }
    }

    public boolean isAdmin() {
        return SandalphonUtils.hasRole("admin");
    }

    public void addActivityLog(ActivityKey activityKey) {
        long time = System.currentTimeMillis();
        ActivityLogServiceImpl.getInstance().addActivityLog(activityKey, SandalphonUtils.getRealUsername(), time, SandalphonUtils.getRealUserJid(), IdentityUtils.getIpAddress());
        String log = SandalphonUtils.getRealUsername() + " " + activityKey.toString();
        try {
            if (JudgelsPlayUtils.hasViewPoint()) {
                log += " view as " +  IdentityUtils.getUsername();
            }
            UserActivityMessageServiceImpl.getInstance().addUserActivityMessage(new UserActivityMessage(System.currentTimeMillis(), SandalphonUtils.getRealUserJid(), log, IdentityUtils.getIpAddress()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void buildInstance(JophielClientAPI jophielClientAPI, JophielPublicAPI jophielPublicAPI) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has already been built");
        }
        INSTANCE = new SandalphonControllerUtils(jophielClientAPI, jophielPublicAPI);
    }

    public static SandalphonControllerUtils getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has not been built");
        }
        return INSTANCE;
    }
}
