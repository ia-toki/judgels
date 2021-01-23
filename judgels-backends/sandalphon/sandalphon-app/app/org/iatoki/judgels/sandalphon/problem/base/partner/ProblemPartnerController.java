package org.iatoki.judgels.sandalphon.problem.base.partner;

import static judgels.service.ServiceUtils.checkFound;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.partner.ProblemPartner;
import org.iatoki.judgels.play.actor.ActorChecker;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels .sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.html.listPartnersView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class ProblemPartnerController extends AbstractProblemController {

    private static final long PAGE_SIZE = 20;

    private final ActorChecker actorChecker;
    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public ProblemPartnerController(
            ActorChecker actorChecker,
            ProblemService problemService,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(Http.Request req, long problemId) {
        return listPartners(req, problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Page<ProblemPartner> pageOfProblemPartners = problemService.getPageOfProblemPartners(problem.getJid(), pageIndex, PAGE_SIZE, orderBy, orderDir);

        Set<String> userJids = pageOfProblemPartners.getPage().stream().map(ProblemPartner::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listPartnersView.render(problem.getId(), pageOfProblemPartners, profilesMap, orderBy, orderDir));
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.ProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Partners");

        return renderPartnerTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result addPartner(Http.Request req, long problemId) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

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
    public Result editPartner(Http.Request req, long problemId, long partnerId) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

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
