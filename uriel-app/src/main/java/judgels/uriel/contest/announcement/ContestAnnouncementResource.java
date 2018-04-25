package judgels.uriel.contest.announcement;

import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.announcement.ContestAnnouncementService;
import judgels.uriel.api.contest.announcement.ContestAnnouncementsResponse;
import judgels.uriel.role.RoleChecker;

public class ContestAnnouncementResource implements ContestAnnouncementService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final ContestAnnouncementStore announcementStore;

    @Inject
    public ContestAnnouncementResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            ContestAnnouncementStore announcementStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.announcementStore = announcementStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestAnnouncementsResponse getAnnouncements(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canViewAnnouncements(actorJid, contestJid));

        return new ContestAnnouncementsResponse.Builder()
                .data(announcementStore.getAnnouncements(contestJid, actorJid))
                .build();
    }
}
