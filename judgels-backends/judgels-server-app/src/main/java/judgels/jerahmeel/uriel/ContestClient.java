package judgels.jerahmeel.uriel;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.service.api.JudgelsServiceException;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.ContestService;

public class ContestClient {
    private final Optional<ContestService> contestService;

    private final LoadingCache<String, ContestInfo> contestByJidCache;
    private final LoadingCache<String, String> slugToJidCache;

    @Inject
    public ContestClient(Optional<ContestService> contestService) {
        this.contestService = contestService;
        this.contestByJidCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build(this::getContestByJidUncached);
        this.slugToJidCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build(this::getContestJidBySlugUncached);
    }

    public Map<String, ContestInfo> getContestsByJids(Iterable<String> contestJids) {
        return contestByJidCache.getAll(contestJids);
    }

    public Map<String, String> translateSlugsToJids(Iterable<String> contestSlugs) {
        return slugToJidCache.getAll(contestSlugs);
    }

    private ContestInfo getContestByJidUncached(String contestJid) {
        if (!contestService.isPresent()) {
            return null;
        }
        try {
            Contest contest = contestService.get().getContest(Optional.empty(), contestJid);
            return new ContestInfo.Builder()
                    .slug(contest.getSlug())
                    .name(contest.getName())
                    .beginTime(contest.getBeginTime())
                    .build();
        } catch (JudgelsServiceException e) {
            return null;
        }
    }

    private String getContestJidBySlugUncached(String contestSlug) {
        if (!contestService.isPresent()) {
            return null;
        }
        try {
            return contestService.get().getContestBySlug(Optional.empty(), contestSlug).getJid();
        } catch (JudgelsServiceException e) {
            return null;
        }
    }
}
