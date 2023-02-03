package judgels.jerahmeel.stats;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.api.stats.UserStats;
import judgels.jerahmeel.api.stats.UserStatsService;
import judgels.jerahmeel.api.stats.UserTopStatsEntry;
import judgels.jerahmeel.api.stats.UserTopStatsResponse;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;

public class UserStatsResource implements UserStatsService {
    private final StatsStore statsStore;
    private final UserClient userClient;

    @Inject
    public UserStatsResource(
            StatsStore statsStore,
            UserClient userClient) {

        this.statsStore = statsStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserTopStatsResponse getTopUserStats(Optional<Integer> page, Optional<Integer> pageSize) {
        Page<UserTopStatsEntry> stats = statsStore.getTopUserStats(page, pageSize);
        Set<String> userJids = stats.getPage().stream().map(UserTopStatsEntry::getUserJid).collect(toSet());
        Map<String, Profile> profileMap = userClient.getProfiles(userJids);

        return new UserTopStatsResponse.Builder()
                .data(stats)
                .profilesMap(profileMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserStats getUserStats(String username) {
        String userJid = checkFound(userClient.translateUsernameToJid(username));
        return statsStore.getUserStats(userJid);
    }
}
