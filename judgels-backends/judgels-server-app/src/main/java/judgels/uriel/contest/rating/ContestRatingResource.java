package judgels.uriel.contest.rating;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.RatingEvent;
import judgels.jophiel.api.user.rating.UserRating;
import judgels.jophiel.api.user.rating.UserRatingEvent;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.rating.UserRatingStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.rating.ContestRating;
import judgels.uriel.api.contest.rating.ContestRatingChanges;
import judgels.uriel.api.contest.rating.ContestRatingHistoryResponse;
import judgels.uriel.api.contest.rating.ContestsPendingRatingResponse;
import judgels.uriel.api.contest.scoreboard.Scoreboard;
import judgels.uriel.api.contest.scoreboard.ScoreboardEntry;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardBuilder;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.scoreboard.RawContestScoreboard;

@Path("/api/v2/contest-rating")
public class ContestRatingResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestScoreboardStore scoreboardStore;
    @Inject protected ContestScoreboardBuilder scoreboardBuilder;
    @Inject protected ContestRatingComputer ratingComputer;
    @Inject protected UserStore userStore;
    @Inject protected UserRatingStore userRatingStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ContestRatingResource() {}

    @GET
    @Path("/pending")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsPendingRatingResponse getContestsPendingRating(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Optional<RatingEvent> latestEvent = userRatingStore.getLatestRatingEvent();
        Instant latestTime = latestEvent.isPresent() ? latestEvent.get().getTime() : Instant.MIN;

        List<Contest> contests = contestStore.getPublicContestsAfter(latestTime);
        Map<String, ContestRatingChanges> ratingChangesMap = contests.stream()
                .collect(Collectors.toMap(Contest::getJid, this::getRatingChanges));

        return new ContestsPendingRatingResponse.Builder()
                .data(contests)
                .ratingChangesMap(ratingChangesMap)
                .build();
    }

    @GET
    @Path("/history")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestRatingHistoryResponse getRatingHistory(@QueryParam("username") String username) {
        String userJid = checkFound(userStore.translateUsernameToJid(username));

        List<UserRatingEvent> userRatingEvents = userRatingStore.getUserRatingEvents(userJid);

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

    private ContestRatingChanges getRatingChanges(Contest contest) {
        Optional<RawContestScoreboard> raw = scoreboardStore.getScoreboard(contest.getJid(), OFFICIAL);
        if (!raw.isPresent()) {
            return new ContestRatingChanges.Builder().build();
        }

        Scoreboard scoreboard = scoreboardBuilder.buildScoreboard(raw.get(), contest, "", true, true);

        Map<String, Integer> ranksMap = scoreboard.getContent().getEntries().stream()
                .filter(ScoreboardEntry::hasSubmission)
                .collect(Collectors.toMap(ScoreboardEntry::getContestantJid, ScoreboardEntry::getRank));

        Map<String, Profile> profilesMap = profileStore.getProfiles(ranksMap.keySet(), contest.getBeginTime());

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

        return new ContestRatingChanges.Builder()
                .ratingsMap(ratingsMap)
                .profilesMap(profilesMap)
                .build();
    }
}
