package org.iatoki.judgels.sandalphon.problem.base.partner;

import static judgels.service.ServiceUtils.checkAllowed;
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
import judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.partner.html.listPartnersView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class ProblemPartnerController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProfileService profileService;

    @Inject
    public ProblemPartnerController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            ProfileService profileService) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(Http.Request req, long problemId) {
        return listPartners(req, problemId, 1, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        Page<ProblemPartner> pageOfProblemPartners = problemStore.getPageOfProblemPartners(problem.getJid(), pageIndex, orderBy, orderDir);

        Set<String> userJids = pageOfProblemPartners.getPage().stream().map(ProblemPartner::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listPartnersView.render(problem.getId(), pageOfProblemPartners, profilesMap, orderBy, orderDir));
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.ProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Partners");

        return renderPartnerTemplate(template, problem);
    }

    @Transactional(readOnly = true)
    public Result addPartner(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

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
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

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
