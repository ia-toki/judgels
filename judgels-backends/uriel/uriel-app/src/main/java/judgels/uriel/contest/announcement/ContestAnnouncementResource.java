package judgels.uriel.contest.announcement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementConfig;
import judgels.uriel.api.contest.announcement.ContestAnnouncementData;
import judgels.uriel.api.contest.announcement.ContestAnnouncementService;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;
import judgels.uriel.contest.ContestStore;

public class ContestAnnouncementResource implements ContestAnnouncementService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestAnnouncementRoleChecker announcementRoleChecker;
    private final ContestAnnouncementStore announcementStore;

    @Inject
    public ContestAnnouncementResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestAnnouncementRoleChecker announcementRoleChecker,
            ContestAnnouncementStore announcementStore) {

        this.actorChecker = actorChecker;
        this.announcementRoleChecker = announcementRoleChecker;
        this.contestStore = contestStore;
        this.announcementStore = announcementStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestAnnouncementsResponse getAnnouncements(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canViewPublishedAnnouncements(actorJid, contest));

        boolean canCreateAnnouncement = announcementRoleChecker.canCreateAnnouncement(actorJid, contest);
        boolean canEditAnnouncement = announcementRoleChecker.canEditAnnouncement(actorJid, contest);

        ContestAnnouncementConfig config = new ContestAnnouncementConfig.Builder()
                .isAllowedToCreateAnnouncement(canCreateAnnouncement)
                .isAllowedToEditAnnouncement(canEditAnnouncement)
                .build();

        List<ContestAnnouncement> data =
                announcementRoleChecker.canViewAllAnnouncements(actorJid, contest)
                        ? announcementStore.getAnnouncements(contestJid)
                        : announcementStore.getPublishedAnnouncements(contestJid);

        return new ContestAnnouncementsResponse.Builder()
                .data(data)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestAnnouncement createAnnouncement(
            AuthHeader authHeader,
            String contestJid,
            ContestAnnouncementData announcementData) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canCreateAnnouncement(actorJid, contest));

        return announcementStore.createAnnouncement(contestJid, announcementData);
    }

    @Override
    @UnitOfWork
    public ContestAnnouncement updateAnnouncement(
            AuthHeader authHeader,
            String contestJid,
            String announcementJid,
            ContestAnnouncementData announcementData) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(announcementRoleChecker.canEditAnnouncement(actorJid, contest));

        return announcementStore.updateAnnouncement(contestJid, announcementJid, announcementData);
    }
}
