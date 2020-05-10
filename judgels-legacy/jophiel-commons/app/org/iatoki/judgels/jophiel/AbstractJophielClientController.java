package org.iatoki.judgels.jophiel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import judgels.jophiel.api.JophielClientConfiguration;
import org.iatoki.judgels.jophiel.logincheck.html.isLoggedOut;
import org.iatoki.judgels.play.AbstractJudgelsController;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.mvc.Http;
import play.mvc.Result;

public class AbstractJophielClientController extends AbstractJudgelsController {
    @Inject
    private JophielClientConfiguration jophielClientConfig;

    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        if (IdentityUtils.getUserJid() != null) {
            template.addAdditionalScript(isLoggedOut.render(getUserIsLoggedInAPIEndpoint(), org.iatoki.judgels.jophiel.routes.JophielClientController.logout(getCurrentUrl(Http.Context.current().request())).absoluteURL(Http.Context.current().request(), Http.Context.current().request().secure()), "lib/jophielcommons/javascripts/isLoggedOut.js", IdentityUtils.getUserJid()));
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
