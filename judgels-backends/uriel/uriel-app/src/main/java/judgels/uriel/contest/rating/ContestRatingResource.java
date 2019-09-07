package judgels.uriel.contest.rating;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.rating.ContestRating;
import judgels.uriel.api.contest.rating.ContestRatingHistoryResponse;
import judgels.uriel.api.contest.rating.ContestRatingService;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardBuilder;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.scoreboard.RawContestScoreboard;

public class ContestRatingResource implements ContestRatingService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final UserSearchService userSearchService;
    private final UserRatingService userRatingService;
    private final ProfileService profileService;
    private final ContestStore contestStore;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestScoreboardBuilder scoreboardBuilder;
    private final ContestRatingComputer ratingComputer;

    @Inject
    public ContestRatingResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            UserSearchService userSearchService,
            UserRatingService userRatingService,
            ProfileService profileService,
            ContestStore contestStore,
            ContestScoreboardStore scoreboardStore,
            ContestScoreboardBuilder scoreboardBuilder,
            ContestRatingComputer ratingComputer) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.userSearchService = userSearchService;
        this.userRatingService = userRatingService;
        this.profileService = profileService;
        this.contestStore = contestStore;
        this.scoreboardStore = scoreboardStore;
        this.scoreboardBuilder = scoreboardBuilder;
        this.ratingComputer = ratingComputer;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Map<String, UserRating> getRatingResult(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Optional<RawContestScoreboard> raw = scoreboardStore.getScoreboard(contest.getJid(), OFFICIAL);
        if (!raw.isPresent()) {
            return ImmutableMap.of();
        }

        Scoreboard scoreboard = scoreboardBuilder.buildScoreboard(raw.get(), contest, "", true, true);

        Map<String, Integer> ranksMap = scoreboard.getContent().getEntries().stream()
                .filter(ScoreboardEntry::hasSubmission)
                .collect(Collectors.toMap(ScoreboardEntry::getContestantJid, ScoreboardEntry::getRank));

        Map<String, Profile> profilesMap = profileService.getProfiles(ranksMap.keySet(), contest.getBeginTime());

        Map<String, Integer> publicRatingsMap = Maps.newHashMap();
        Map<String, Integer> hiddenRatingsMap = Maps.newHashMap();

        List<String> contestantJids = Lists.newArrayList();

        for (Map.Entry<String, Profile> entry : profilesMap.entrySet()) {
            String contestantJid = entry.getKey();
            Optional<UserRating> rating = entry.getValue().getRating();
            int publicRating = rating.map(UserRating::getPublicRating).orElse(UserRating.INITIAL_RATING);
            int hiddenRating = rating.map(UserRating::getHiddenRating).orElse(UserRating.INITIAL_RATING);

            publicRatingsMap.put(contestantJid, publicRating);
            hiddenRatingsMap.put(contestantJid, hiddenRating);
            contestantJids.add(contestantJid);
        }

        Map<String, UserRating> ratingsMap =
                ratingComputer.compute(contestantJids, ranksMap, publicRatingsMap, hiddenRatingsMap);

        return ratingsMap.entrySet().stream()
                .collect(Collectors.toMap(e -> profilesMap.get(e.getKey()).getUsername(), e -> e.getValue()));
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

        Map<String, ContestInfo> contestInfosMap = contestStore.getContestInfosByJids(contestJids);

        List<ContestRating> data = userRatingEvents.stream()
                .map(e -> new ContestRating.Builder()
                        .contestJid(e.getEventJid())
                        .rating(e.getRating())
                        .build())
                .collect(Collectors.toList());

        return new ContestRatingHistoryResponse.Builder()
                .data(data)
                .contestsMap(contestInfosMap)
                .build();
    }
}
