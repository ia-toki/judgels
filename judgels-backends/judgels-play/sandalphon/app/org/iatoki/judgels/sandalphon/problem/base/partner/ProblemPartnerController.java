package org.iatoki.judgels.sandalphon.problem.base.partner;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.html.listPartnersView;
import play.db.jpa.Transactional;
import play.mvc.Result;

@Singleton
public class ProblemPartnerController extends AbstractProblemController {

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

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listPartnersView.render(problem.getId(), pageOfProblemPartners, orderBy, orderDir));
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.ProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Partners");

        return renderPartnerTemplate(template, problemService, problem);
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
