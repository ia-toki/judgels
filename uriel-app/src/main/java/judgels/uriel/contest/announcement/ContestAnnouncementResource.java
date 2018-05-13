package judgels.uriel.contest.announcement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.announcement.ContestAnnouncementService;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.role.RoleChecker;

public class ContestAnnouncementResource implements ContestAnnouncementService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestAnnouncementStore announcementStore;

    @Inject
    public ContestAnnouncementResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestStore contestStore,
            ContestAnnouncementStore announcementStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.announcementStore = announcementStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestAnnouncementsResponse getAnnouncements(Optional<AuthHeader> authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.findContestByJid(contestJid));
        checkAllowed(roleChecker.canViewAnnouncements(actorJid, contest));

        return new ContestAnnouncementsResponse.Builder()
                .data(announcementStore.getAnnouncements(contestJid, actorJid))
                .build();
    }
}
