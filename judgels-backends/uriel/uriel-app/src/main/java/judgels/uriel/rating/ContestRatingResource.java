package judgels.uriel.rating;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.rating.ContestInfo;
import judgels.uriel.api.contest.rating.ContestRating;
import judgels.uriel.api.contest.rating.ContestRatingHistoryResponse;
import judgels.uriel.api.contest.rating.ContestRatingService;
import judgels.uriel.contest.ContestStore;

public class ContestRatingResource implements ContestRatingService {
    private UserSearchService userSearchService;
    private UserRatingService userRatingService;
    private ContestStore contestStore;

    @Inject
    public ContestRatingResource(
            UserSearchService userSearchService,
            UserRatingService userRatingService,
            ContestStore contestStore) {
        this.userSearchService = userSearchService;
        this.userRatingService = userRatingService;
        this.contestStore = contestStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestRatingHistoryResponse getRatingHistory(String username) {
        Set<String> usernameSet = Stream.of(username).collect(Collectors.toSet());
        Map<String, String> userJidMap = userSearchService.translateUsernamesToJids(usernameSet);

        if (!userJidMap.containsKey(username)) {
            throw new NotFoundException();
        }

        String userJid = userJidMap.get(username);
        List<UserRatingEvent> userRatingEvents = userRatingService.getRatingHistory(userJid);

        Set<String> contestJids = userRatingEvents.stream()
                .map(UserRatingEvent::getEventJid)
                .collect(Collectors.toSet());

        Map<String, Contest> contestMap = contestStore.getContestByJids(contestJids);

        List<ContestRating> data = userRatingEvents.stream()
                .map(e -> new ContestRating.Builder()
                        .contestJid(e.getEventJid())
                        .rating(e.getPublicRating())
                        .build())
                .collect(Collectors.toList());

        Map<String, ContestInfo> contestInfoMap = contestMap.values().stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        c -> new ContestInfo.Builder()
                                .slug(c.getSlug())
                                .name(c.getName())
                                .beginTime(c.getBeginTime())
                                .build()
                ));

        return new ContestRatingHistoryResponse.Builder()
                .data(data)
                .contestMap(contestInfoMap)
                .build();
    }
}
