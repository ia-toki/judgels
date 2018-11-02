package judgels.uriel.contest.announcement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
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

public class ContestAnnouncementResource implements ContestAnnouncementService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestAnnouncementRoleChecker announcementRoleChecker;
    private final ContestAnnouncementStore announcementStore;
    private final ProfileService profileService;

    @Inject
    public ContestAnnouncementResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestAnnouncementRoleChecker announcementRoleChecker,
            ContestAnnouncementStore announcementStore,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.announcementRoleChecker = announcementRoleChecker;
        this.contestStore = contestStore;
        this.announcementStore = announcementStore;
        this.profileService = profileService;
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
        ContestAnnouncementConfig config = new ContestAnnouncementConfig.Builder()
                .canSupervise(canSupervise)
                .build();

        Page<ContestAnnouncement> data = canSupervise
                ? announcementStore.getAnnouncements(contestJid, page)
                : announcementStore.getPublishedAnnouncements(contestJid, page);

        Set<String> userJids = data.getData()
                .stream()
                .map(ContestAnnouncement::getUserJid)
                .collect(Collectors.toSet());

        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        return new ContestAnnouncementsResponse.Builder()
                .data(data)
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
        checkAllowed(announcementRoleChecker.canSupervise(actorJid, contest));

        return announcementStore.createAnnouncement(contestJid, data);
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
        checkAllowed(announcementRoleChecker.canSupervise(actorJid, contest));

        return announcementStore.updateAnnouncement(contestJid, announcementJid, data);
    }
}
