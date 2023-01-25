package judgels.uriel.contest.history;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.user.UserClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.history.ContestHistoryEvent;
import judgels.uriel.api.contest.history.ContestHistoryResponse;
import judgels.uriel.api.contest.history.ContestHistoryService;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;

public class ContestHistoryResource implements ContestHistoryService {
    private final UserClient userClient;
    private final UserRatingService userRatingService;
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;

    @Inject
    public ContestHistoryResource(
            UserClient userClient,
            UserRatingService userRatingService,
            ContestStore contestStore,
            ContestContestantStore contestantStore) {

        this.userClient = userClient;
        this.userRatingService = userRatingService;
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestHistoryResponse getPublicHistory(String username) {
        String userJid = checkFound(userClient.translateUsernameToJid(username));

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
