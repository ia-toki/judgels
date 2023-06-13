package judgels.michael.problem.partner;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.problem.BaseProblemResource;
import judgels.michael.resource.EditPartnersForm;
import judgels.michael.resource.EditPartnersView;
import judgels.michael.resource.ListPartnersView;
import judgels.michael.resource.PartnerUtils;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.problem.base.partner.ProblemPartnerStore;

@Path("/problems/{problemId}/partners")
public class ProblemPartnerResource extends BaseProblemResource {
    @Inject protected ProblemPartnerStore partnerStore;

    @Inject public ProblemPartnerResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listPartners(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.isAuthorOrAbove(actor, problem));

        List<Partner> partners = partnerStore.getPartners(problem.getJid());
        Set<String> userJids = partners.stream().map(Partner::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        HtmlTemplate template = newProblemPartnerTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ListPartnersView(template, partners, profilesMap);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editPartners(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.isAuthorOrAbove(actor, problem));

        List<Partner> partners = partnerStore.getPartners(problem.getJid());
        Set<String> userJids = partners.stream().map(Partner::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        EditPartnersForm form = new EditPartnersForm();
        form.csv = PartnerUtils.partnersToCsv(partners, profilesMap);

        return renderEditPartners(actor, problem, form);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updatePartners(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditPartnersForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.isAuthorOrAbove(actor, problem));

        Optional<List<Partner>> partners = PartnerUtils.csvToPartners(form.csv, userStore);
        if (!partners.isPresent()) {
            form.globalError = "Invalid CSV format.";
            return ok(renderEditPartners(actor, problem, form));
        }

        partnerStore.setPartners(problem.getJid(), partners.get());

        return redirect("/problems/" + problemId + "/partners");
    }

    private View renderEditPartners(Actor actor, Problem problem, EditPartnersForm form) {
        HtmlTemplate template = newProblemPartnerTemplate(actor, problem);
        template.setActiveSecondaryTab("edit");
        return new EditPartnersView(template, form);
    }

    private HtmlTemplate newProblemPartnerTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("partners");
        template.addSecondaryTab("view", "View", "/problems/" + problem.getId() + "/partners");
        template.addSecondaryTab("edit", "Edit", "/problems/" + problem.getId() + "/partners/edit");
        return template;
    }
}
