package org.iatoki.judgels.jophiel;

import com.palantir.conjure.java.api.errors.RemoteException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.play.PlaySession;
import judgels.jophiel.api.play.PlaySessionErrors;
import judgels.jophiel.api.play.PlaySessionService;
import judgels.jophiel.api.session.Credentials;
import org.iatoki.judgels.jophiel.account.LoginForm;
import org.iatoki.judgels.jophiel.account.html.loginView;
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

    @Inject
    public JophielClientController(PlaySessionService sessionService) {
        this.sessionService = sessionService;
    }

    @AddCSRFToken
    public Result login() {
        Form<LoginForm> form = formFactory.form(LoginForm.class);
        return showLogin(form);
    }

    @RequireCSRFCheck
    @Transactional
    public Result postLogin() {
        Form<LoginForm> form = formFactory.form(LoginForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return showLogin(form);
        }

        LoginForm data = form.get();

        PlaySession session;
        try {
            session = sessionService.logIn(Credentials.of(data.username, data.password));
        } catch (RemoteException e) {
            if (e.getError().errorName().equals(PlaySessionErrors.ROLE_NOT_ALLOWED.name())) {
                form.reject("User role not allowed to log in.");
            } else if (e.getStatus() == 403) {
                form.reject("Username or password incorrect.");
            }
            return showLogin(form);
        }

        session("version", JophielSessionUtils.getSessionVersion());
        session("token", session.getToken());
        session("userJid", session.getUserJid());
        session("username", session.getUsername());
        session("role", session.getRole());
        session("avatar", getUserAvatarUrl(session.getUserJid()));

        return redirect(getServiceLoginUrl(session.getAuthCode(), getRootUrl(Http.Context.current().request())));
    }

    public Result logout(String returnUri) {
        session().clear();
        return redirect(getServiceLogoutUrl(returnUri));
    }

    private Result showLogin(Form<LoginForm> form) {
        HtmlTemplate template = getBaseHtmlTemplate();

        template.setSingleColumn();
        template.setContent(loginView.render(form));
        template.setMainTitle("Log in");
        template.markBreadcrumbLocation("Log in", routes.JophielClientController.login());
        template.setPageTitle("Log in");

        return renderTemplate(template);
    }
}
