package org.iatoki.judgels.jophiel;

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.play.PlaySession;
import judgels.jophiel.api.play.PlaySessionErrors;
import judgels.jophiel.api.play.PlaySessionService;
import judgels.jophiel.api.session.Credentials;
import judgels.service.api.JudgelsServiceException;
import org.iatoki.judgels.jophiel.account.LoginForm;
import org.iatoki.judgels.jophiel.account.html.loginView;
import org.iatoki.judgels.play.actor.ActorChecker;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class JophielClientController extends AbstractJophielClientController {
    private final PlaySessionService sessionService;
    private final ActorChecker actorChecker;

    @Inject
    public JophielClientController(PlaySessionService sessionService, ActorChecker actorChecker) {
        this.sessionService = sessionService;
        this.actorChecker = actorChecker;
    }

    @AddCSRFToken
    public Result login(Http.Request req) {
        Form<LoginForm> form = formFactory.form(LoginForm.class);
        return showLogin(req, form);
    }

    @RequireCSRFCheck
    @Transactional
    public Result postLogin(Http.Request req) {
        Form<LoginForm> form = formFactory.form(LoginForm.class).bindFromRequest(req);
        if (form.hasErrors()) {
            return showLogin(req, form);
        }

        LoginForm data = form.get();

        PlaySession session;
        try {
            session = sessionService.logIn(Credentials.of(data.username, data.password));
        } catch (JudgelsServiceException e) {
            if (e.getMessage().equals(PlaySessionErrors.ROLE_NOT_ALLOWED)) {
                form = form.withGlobalError("User role not allowed to log in.");
            } else if (e.getCode() == 403) {
                form = form.withGlobalError("Username or password incorrect.");
            }
            return showLogin(req, form);
        }

        return redirect(getServiceLoginUrl(session.getAuthCode(), getRootUrl(req)))
                .removingFromFlash("localChangesError")
                .withSession(new ImmutableMap.Builder<String, String>()
                        .put("version", JophielSessionUtils.getSessionVersion())
                        .put("token", session.getToken())
                        .put("userJid", session.getUserJid())
                        .put("username", session.getUsername())
                        .put("role", session.getRole())
                        .put("avatar", getUserAvatarUrl(session.getUserJid()))
                        .build());
    }

    public Result logout(String returnUri) {
        actorChecker.clear();
        return redirect(getServiceLogoutUrl(returnUri))
                .withNewSession();
    }

    private Result showLogin(Http.Request req, Form<LoginForm> form) {
        HtmlTemplate template = getBaseHtmlTemplate(req);

        template.setSingleColumn();
        template.setContent(loginView.render(form));
        template.setMainTitle("Log in");
        template.markBreadcrumbLocation("Log in", routes.JophielClientController.login());
        template.setPageTitle("Log in");

        return renderTemplate(template);
    }
}
