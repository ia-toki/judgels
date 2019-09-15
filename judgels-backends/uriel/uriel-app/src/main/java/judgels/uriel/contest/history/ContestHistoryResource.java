package judgels.uriel.contest.history;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.history.ContestHistoryEvent;
import judgels.uriel.api.contest.history.ContestHistoryResponse;
import judgels.uriel.api.contest.history.ContestHistoryService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;

public class ContestHistoryResource implements ContestHistoryService {
    private final UserSearchService userSearchService;
    private final UserRatingService userRatingService;
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestHistoryResource(
            UserSearchService userSearchService,
            UserRatingService userRatingService,
            ContestStore contestStore,
            ContestContestantStore contestantStore) {

        this.userSearchService = userSearchService;
        this.userRatingService = userRatingService;
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestHistoryResponse getPublicHistory(String username) {
        Map<String, String> userJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));
        if (!userJidMap.containsKey(username)) {
            throw new NotFoundException();
        }
        String userJid = userJidMap.get(username);

        List<Contest> contests = contestStore.getPubliclyParticipatedContests(userJid);
        Map<String, UserRating> ratingsMap = userRatingService.getRatingHistory(userJid)
                .stream()
                .collect(Collectors.toMap(UserRatingEvent::getEventJid, UserRatingEvent::getRating));
        Map<String, Integer> ranksMap = contestantStore.getContestantFinalRanks(userJid);

        List<ContestHistoryEvent> events = contests
                .stream()
                .map(contest -> new ContestHistoryEvent.Builder()
                        .contestJid(contest.getJid())
                        .rating(Optional.ofNullable(ratingsMap.get(contest.getJid())))
                        .rank(ranksMap.get(contest.getJid()))
                        .build())
                .collect(Collectors.toList());

        Map<String, ContestInfo> contestsMap = contests
                .stream()
                .collect(Collectors.toMap(Contest::getJid, contest -> new ContestInfo.Builder()
                        .name(contest.getName())
                        .slug(contest.getSlug())
                        .beginTime(contest.getBeginTime())
                        .build()));

        return new ContestHistoryResponse.Builder()
                .data(events)
                .contestsMap(contestsMap)
                .build();
    }
}
