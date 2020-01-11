package judgels.jerahmeel.user;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.api.user.UserStats;
import judgels.jerahmeel.api.user.UserStatsService;
import judgels.jerahmeel.api.user.UserTopStatsEntry;
import judgels.jerahmeel.api.user.UserTopStatsResponse;
import judgels.jerahmeel.stats.StatsStore;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;

public class UserStatsResource implements UserStatsService {
    private final StatsStore statsStore;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public UserStatsResource(
            StatsStore statsStore,
            UserSearchService userSearchService,
            ProfileService profileService) {

        this.statsStore = statsStore;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserTopStatsResponse getTopUserStats(Optional<Integer> page, Optional<Integer> pageSize) {
        Page<UserTopStatsEntry> stats = statsStore.getTopUserStats(page, pageSize);
        Set<String> userJids = stats.getPage().stream().map(UserTopStatsEntry::getUserJid).collect(toSet());
        Map<String, Profile> profileMap = profileService.getProfiles(userJids);

        return new UserTopStatsResponse.Builder()
                .data(stats)
                .profilesMap(profileMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserStats getUserStats(String username) {
        String userJid = checkFound(Optional.ofNullable(
                userSearchService.translateUsernamesToJids(ImmutableSet.of(username)).get(username)));
        return statsStore.getUserStats(userJid);
    }
}
