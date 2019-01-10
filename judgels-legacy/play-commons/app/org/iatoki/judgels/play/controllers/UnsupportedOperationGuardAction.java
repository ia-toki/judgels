package org.iatoki.judgels.play.controllers;

import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.baseLayout;
import org.iatoki.judgels.play.views.html.layouts.centerLayout;
import org.iatoki.judgels.play.views.html.layouts.headerFooterLayout;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.messageView;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public final class UnsupportedOperationGuardAction extends Action<UnsupportedOperationGuard> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            return this.delegate.call(context);
        } catch (UnsupportedOperationException e) {
            return showUnsupportedOperation(e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                return showUnsupportedOperation(e.getCause());
            } else {
                throw e;
            }
        }
    }

    private F.Promise<Result> showUnsupportedOperation(Throwable e) {
        return F.Promise.promise(() -> {
                LazyHtml content = new LazyHtml(messageView.render(Messages.get("commons.unsupportedOperation.message")));
                content.appendLayout(c -> headingLayout.render(Messages.get("commons.unsupportedOperation"), c));
                content.appendLayout(c -> centerLayout.render(c));
                content.appendLayout(c -> headerFooterLayout.render(c));
                content.appendLayout(c -> baseLayout.render("commons.unsupportedOperation", c));
                return Results.badRequest(content.render());
            }
        );
    }
}
