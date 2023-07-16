package judgels.uriel.contest.announcement;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.api.contest.announcement.ContestAnnouncementStatus.PUBLISHED;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementConfig;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

@Path("/api/v2/contests/{contestJid}/announcements")
public class ContestAnnouncementResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestAnnouncementRoleChecker announcementRoleChecker;
    @Inject protected ContestAnnouncementStore announcementStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestAnnouncementResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestAnnouncementsResponse getAnnouncements(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canViewPublished(actorJid, contest));

        boolean canSupervise = announcementRoleChecker.canSupervise(actorJid, contest);
        boolean canManage = announcementRoleChecker.canManage(actorJid, contest);
        ContestAnnouncementConfig config = new ContestAnnouncementConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(canManage)
                .build();

        Optional<String> statusFilter = canSupervise ? Optional.empty() : Optional.of(PUBLISHED.name());
        Page<ContestAnnouncement> announcements = announcementStore.getAnnouncements(contestJid, statusFilter, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(announcements.getPage(), ContestAnnouncement::getUserJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids, contest.getBeginTime());

        contestLogger.log(contestJid, "OPEN_ANNOUNCEMENTS");

        return new ContestAnnouncementsResponse.Builder()
                .data(announcements)
                .config(config)
                .profilesMap(profilesMap)
                .build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public ContestAnnouncement createAnnouncement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestAnnouncementData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canManage(actorJid, contest));
        ContestAnnouncement announcement = announcementStore.createAnnouncement(contestJid, data);

        contestLogger.log(contestJid, "CREATE_ANNOUNCEMENT", announcement.getJid());

        return announcement;
    }

    @PUT
    @Path("/{announcementJid}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public ContestAnnouncement updateAnnouncement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @PathParam("announcementJid") String announcementJid,
            ContestAnnouncementData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canManage(actorJid, contest));

        contestLogger.log(contestJid, "UPDATE_ANNOUNCEMENT", announcementJid);

        return announcementStore.updateAnnouncement(contestJid, announcementJid, data);
    }
}
