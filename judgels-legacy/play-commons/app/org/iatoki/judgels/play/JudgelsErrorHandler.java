package org.iatoki.judgels.play;

import org.iatoki.judgels.play.general.GeneralConfig;
import org.iatoki.judgels.play.sponsor.SponsorConfig;
import org.iatoki.judgels.play.template.LazyHtml;
import org.iatoki.judgels.play.template.base.html.baseLayout;
import org.iatoki.judgels.play.template.base.html.centerLayout;
import org.iatoki.judgels.play.template.base.html.headerFooterLayout;
import org.iatoki.judgels.play.template.content.html.headingLayout;
import org.iatoki.judgels.play.template.content.html.messageView;
import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;

public final class JudgelsErrorHandler extends DefaultHttpErrorHandler {

    private final GeneralConfig generalConfig;

    @com.google.inject.Inject(optional = true)
    protected SponsorConfig sponsorConfig;

    @Inject
    public JudgelsErrorHandler(Configuration configuration, Environment environment, OptionalSourceMapper optionalSourceMapper, Provider<Router> provider, GeneralConfig generalConfig) {
        super(configuration, environment, optionalSourceMapper, provider);

        this.generalConfig = generalConfig;
    }

    @Override
    protected F.Promise<Result> onBadRequest(Http.RequestHeader requestHeader, String message) {
        if (message.contains("Cannot parse")) {
            return onNotFound(requestHeader, message);
        }
        return super.onBadRequest(requestHeader, message);
    }

    @Override
    protected F.Promise<Result> onNotFound(Http.RequestHeader requestHeader, String message) {
        return F.Promise.promise(() -> {
                LazyHtml content = new LazyHtml(messageView.render(Messages.get("commons.pageNotFound.message")));
                content.appendLayout(c -> headingLayout.render(Messages.get("commons.pageNotFound"), c));
                content.appendLayout(c -> centerLayout.render(c));
                content.appendLayout(c -> headerFooterLayout.render(generalConfig, sponsorConfig, c));
                content.appendLayout(c -> baseLayout.render("commons.pageNotFound", generalConfig, null, null, c));
                return Results.notFound(content.render());
            }
        );
    }
}
