package org.iatoki.judgels.jophiel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import judgels.jophiel.api.JophielClientConfiguration;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.AbstractJudgelsController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.mvc.Http;
import play.mvc.Result;

public class AbstractJophielClientController extends AbstractJudgelsController {
    @Inject
    private JophielClientConfiguration jophielClientConfig;

    protected String getUserJid(Http.Request req) {
        return JophielSessionUtils.getUserJid(req);
    }

    protected String getUsername(Http.Request req) {
        return JophielSessionUtils.getUsername(req);
    }

    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        Http.Request req = template.getRequest();
        String userJid = getUserJid(req);
        if (userJid != null) {
            template.addAdditionalScript(isLoggedOut.render(getUserIsLoggedInAPIEndpoint(), org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(req)).absoluteURL(req, req.secure()), "javascripts/isLoggedOut.js", userJid));
        }
        return super.renderTemplate(template);
    }

    protected String getUserIsLoggedInAPIEndpoint() {
        return jophielClientConfig.getBaseUrl() + "/api/play/session/is-logged-in";
    }

    protected String getUserAvatarUrl(String userJid) {
        return jophielClientConfig.getBaseUrl() + "/api/v2/users/" + userJid + "/avatar";
    }

    protected String getUserAutocompleteAPIEndpoint() {
        return jophielClientConfig.getBaseUrl() + "/api/v2/users/autocomplete";
    }

    protected String getServiceLoginUrl(String authCode, String returnUri) {
        try {
            return jophielClientConfig.getBaseUrl() + "/api/play/session/client-login/" + URLEncoder.encode(authCode, "UTF-8") + "/" + URLEncoder.encode(returnUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    protected String getServiceLogoutUrl(String returnUri) {
        try {
            return jophielClientConfig.getBaseUrl() + "/api/play/session/client-logout/" + URLEncoder.encode(returnUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
