package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.heading3WithActionLayout;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.html.listPartnersView;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public class ProblemPartnerController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final ProblemService problemService;

    @Inject
    public ProblemPartnerController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(long problemId) throws ProblemNotFoundException {
        return listPartners(problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(long problemId, long pageIndex, String orderBy, String orderDir) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Page<ProblemPartner> pageOfProblemPartners = problemService.getPageOfProblemPartners(problem.getJid(), pageIndex, PAGE_SIZE, orderBy, orderDir);

        LazyHtml content = new LazyHtml(listPartnersView.render(problem.getId(), pageOfProblemPartners, orderBy, orderDir));
        content.appendLayout(c -> heading3WithActionLayout.render(Messages.get("problem.partner.list"), new InternalLink(Messages.get("problem.partner.add"), routes.ProblemPartnerController.addPartner(problem.getId())), c));
        ProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        ProblemPartnerControllerUtils.appendBreadcrumbsLayout(content, problem, new InternalLink(Messages.get("problem.partner.list"), routes.ProblemPartnerController.viewPartners(problem.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Partners");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result addPartner(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        switch (problem.getType()) {
            case PROGRAMMING:
                return redirect(org.iatoki.judgels.sandalphon.problem.programming.partner.routes.ProgrammingProblemPartnerController.addPartner(problem.getId()));
            case BUNDLE:
                return redirect(org.iatoki.judgels.sandalphon.problem.bundle.partner.routes.BundleProblemPartnerController.addPartner(problem.getId()));
            default:
                return badRequest();
        }
    }

    @Transactional(readOnly = true)
    public Result editPartner(long problemId, long partnerId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        switch (problem.getType()) {
            case PROGRAMMING:
                return redirect(org.iatoki.judgels.sandalphon.problem.programming.partner.routes.ProgrammingProblemPartnerController.editPartner(problem.getId(), partnerId));
            case BUNDLE:
                return redirect(org.iatoki.judgels.sandalphon.problem.bundle.partner.routes.BundleProblemPartnerController.editPartner(problem.getId(), partnerId));
            default:
                return badRequest();
        }
    }
}
