package judgels.uriel.contest.announcement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementConfig;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementService;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

public class ContestAnnouncementResource implements ContestAnnouncementService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestAnnouncementRoleChecker announcementRoleChecker;
    private final ContestAnnouncementStore announcementStore;
    private final UserClient userClient;

    @Inject
    public ContestAnnouncementResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestAnnouncementRoleChecker announcementRoleChecker,
            ContestAnnouncementStore announcementStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.announcementRoleChecker = announcementRoleChecker;
        this.contestStore = contestStore;
        this.announcementStore = announcementStore;
        this.contestLogger = contestLogger;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestAnnouncementsResponse getAnnouncements(
            Optional<AuthHeader> authHeader,
            String contestJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canViewPublished(actorJid, contest));

        boolean canSupervise = announcementRoleChecker.canSupervise(actorJid, contest);
        boolean canManage = announcementRoleChecker.canManage(actorJid, contest);
        ContestAnnouncementConfig config = new ContestAnnouncementConfig.Builder()
                .canSupervise(canSupervise)
                .canManage(canManage)
                .build();

        Page<ContestAnnouncement> announcements = canSupervise
                ? announcementStore.getAnnouncements(contestJid, page)
                : announcementStore.getPublishedAnnouncements(contestJid, page);

        Set<String> userJids = announcements.getPage()
                .stream()
                .map(ContestAnnouncement::getUserJid)
                .collect(Collectors.toSet());

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

        contestLogger.log(contestJid, "OPEN_ANNOUNCEMENTS");

        return new ContestAnnouncementsResponse.Builder()
                .data(announcements)
                .config(config)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestAnnouncement createAnnouncement(
            AuthHeader authHeader,
            String contestJid,
            ContestAnnouncementData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canManage(actorJid, contest));
        ContestAnnouncement announcement = announcementStore.createAnnouncement(contestJid, data);

        contestLogger.log(contestJid, "CREATE_ANNOUNCEMENT", announcement.getJid());

        return announcement;
    }

    @Override
    @UnitOfWork
    public ContestAnnouncement updateAnnouncement(
            AuthHeader authHeader,
            String contestJid,
            String announcementJid,
            ContestAnnouncementData data) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canManage(actorJid, contest));

        contestLogger.log(contestJid, "UPDATE_ANNOUNCEMENT", announcementJid);

        return announcementStore.updateAnnouncement(contestJid, announcementJid, data);
    }
}
