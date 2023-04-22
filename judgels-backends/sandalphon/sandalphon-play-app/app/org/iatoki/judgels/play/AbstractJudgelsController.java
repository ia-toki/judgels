package org.iatoki.judgels.play;

import com.google.inject.Inject;
import org.iatoki.judgels.play.general.GeneralConfig;
import play.mvc.Controller;
import play.mvc.Http;

public abstract class AbstractJudgelsController extends Controller {

    @Inject
    protected GeneralConfig generalConfig;

    protected static String getRootUrl(Http.Request request) {
        return "http" + (request.secure() ? "s" : "") + "://" + request.host();
    }

    protected static String getCurrentUrl(Http.Request request) {
        return "http" + (request.secure() ? "s" : "") + "://" + request.host() + request.uri();
    }
}
