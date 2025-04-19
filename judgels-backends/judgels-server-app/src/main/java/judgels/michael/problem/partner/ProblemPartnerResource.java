package judgels.michael.problem.partner;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        var userJids = Lists.transform(partners, Partner::getUserJid);
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

        var userJids = Lists.transform(partners, Partner::getUserJid);
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
