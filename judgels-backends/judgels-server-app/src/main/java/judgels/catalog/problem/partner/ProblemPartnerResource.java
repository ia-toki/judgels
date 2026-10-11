package judgels.catalog.problem.partner;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import judgels.api.catalog.Partner;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemErrors;
import judgels.api.catalog.problem.partner.ProblemPartnerData;
import judgels.api.catalog.problem.partner.ProblemPartnersResponse;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.user.UserStore;

@Path("/api/v4/problems/{problemJid}/partners")
public class ProblemPartnerResource {
    private static final int MAX_PARTNERS = 100;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemPartnerStore partnerStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public ProblemPartnerResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemPartnersResponse getPartners(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.isAuthorOrAbove(actorJid, problem));

        List<Partner> partners = partnerStore.getPartners(problemJid);

        return new ProblemPartnersResponse.Builder()
                .data(partners)
                .profilesMap(profileStore.getProfiles(Lists.transform(partners, Partner::getUserJid)))
                .build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setPartners(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            List<ProblemPartnerData> data) {

        String actorJid = actorChecker.check(authHeader);
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        checkAllowed(roleChecker.isAuthorOrAbove(actorJid, problem));

        Set<String> usernames = data.stream().map(ProblemPartnerData::getUsername).collect(Collectors.toSet());
        if (usernames.size() != data.size()) {
            throw new BadRequestException();
        }
        if (data.size() > MAX_PARTNERS) {
            throw new BadRequestException();
        }

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(usernames);

        Set<String> usernamesNotFound = Sets.difference(usernames, usernameToJidMap.keySet());
        if (!usernamesNotFound.isEmpty()) {
            throw ProblemErrors.partnerUsernamesNotFound(new TreeSet<>(usernamesNotFound));
        }

        partnerStore.setPartners(problemJid, data.stream()
                .map(partner -> new Partner.Builder()
                        .userJid(usernameToJidMap.get(partner.getUsername()))
                        .permission(partner.getPermission())
                        .build())
                .collect(Collectors.toList()));
    }
}
