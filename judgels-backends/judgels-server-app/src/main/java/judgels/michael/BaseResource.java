package judgels.michael;

import io.dropwizard.views.View;
import java.net.URI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import judgels.JudgelsAppConfiguration;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.user.UserRoleChecker;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;

public abstract class BaseResource {
    @Inject protected JudgelsAppConfiguration appConfig;
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker userRoleChecker;

    protected Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    protected Response ok(View v) {
        return Response.ok(v).build();
    }

    protected Response redirect(String url) {
        return Response.seeOther(URI.create(url)).build();
    }

    protected void setCurrentStatementLanguage(HttpServletRequest req, String language) {
        req.getSession().setAttribute("statementLanguage", language);
    }

    protected HtmlTemplate newTemplate() {
        return new HtmlTemplate(appConfig.getName());
    }

    protected HtmlTemplate newTemplate(Actor actor) {
        HtmlTemplate template = newTemplate();

        template.setUsername(actor.getUsername());
        template.setAvatarUrl(actor.getAvatarUrl());

        if (userRoleChecker.canAdminister(actor.getUserJid())) {
            template.addSidebarMenu("accounts", "Accounts", "/accounts/users");
        }

        template.addSidebarMenu("problems", "Problems", "/problems");
        template.addSidebarMenu("lessons", "Lessons", "/lessons");
        return template;
    }
}
