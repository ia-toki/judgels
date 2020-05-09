package org.iatoki.judgels.sandalphon;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.iatoki.judgels.play.AbstractJudgelsController;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import play.mvc.Result;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ApplicationController extends AbstractJudgelsController {
    @Inject
    public ApplicationController() {}

    public Result index() {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
    }
}
