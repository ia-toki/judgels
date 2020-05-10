package org.iatoki.judgels.sandalphon;

import javax.inject.Inject;
import javax.inject.Singleton;
import play.mvc.Result;

@Singleton
public final class ApplicationController extends AbstractSandalphonController {
    @Inject
    public ApplicationController() {}

    public Result index() {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
    }
}
