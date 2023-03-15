package judgels.michael.problem.base.partner;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
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
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.partner.PartnerPermission;
import judgels.sandalphon.api.problem.partner.ProblemPartnerV2;
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
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        List<ProblemPartnerV2> partners = partnerStore.getPartners(problem.getJid());
        Set<String> userJids = partners.stream().map(ProblemPartnerV2::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        HtmlTemplate template = newProblemPartnerTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ListPartnersView(template, partners, profilesMap);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editPartners(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        List<ProblemPartnerV2> partners = partnerStore.getPartners(problem.getJid());
        Set<String> userJids = partners.stream().map(ProblemPartnerV2::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(Instant.now(), userJids);

        EditPartnersForm form = new EditPartnersForm();
        form.csv = partnersToCsv(partners, profilesMap);

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
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.canEdit(actor, problem));

        Optional<List<ProblemPartnerV2>> partners = csvToPartners(form.csv);
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

    private String partnersToCsv(List<ProblemPartnerV2> partners, Map<String, Profile> profilesMap) {
        return partners
                .stream()
                .map(partner -> profilesMap.get(partner.getUserJid()).getUsername() + "," + partner.getPermission())
                .collect(joining("\r\n"));
    }

    private Optional<List<ProblemPartnerV2>> csvToPartners(String csv) {
        List<String> usernames = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (String line : csv.replaceAll("\r", "").split("\n")) {
            String[] tokens = line.split(",");
            if (tokens.length == 0) {
                continue;
            }

            String username = tokens[0].trim();
            if (username.isEmpty()) {
                continue;
            }

            String permission = "UPDATE";
            if (tokens.length == 2) {
                permission = tokens[1].trim();
            } else if (tokens.length > 2) {
                return Optional.empty();
            }

            usernames.add(username);
            permissions.add(permission);
        }

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(new HashSet<>(usernames));

        List<ProblemPartnerV2> partners = new ArrayList<>();
        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            PartnerPermission permission;

            if (!usernameToJidMap.containsKey(username)) {
                continue;
            }
            String userJid = usernameToJidMap.get(username);

            try {
                permission = PartnerPermission.valueOf(permissions.get(i));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }

            partners.add(new ProblemPartnerV2.Builder()
                    .userJid(userJid)
                    .permission(permission)
                    .build());

            if (partners.size() > 100) {
                return Optional.empty();
            }
        }

        return Optional.of(partners);
    }
}
