package org.iatoki.judgels.play.controllers;

import play.data.Form;
import play.mvc.Controller;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
@EntityNotFoundGuard
@UnsupportedOperationGuard
public abstract class AbstractJudgelsController extends Controller {
    protected static boolean formHasErrors(Form form) {
        return form.hasErrors() || form.hasGlobalErrors();
    }
}
