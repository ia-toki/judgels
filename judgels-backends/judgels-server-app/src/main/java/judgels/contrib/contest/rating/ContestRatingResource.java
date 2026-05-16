package judgels.contrib.contest.rating;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestInfo;
import judgels.api.contest.scoreboard.Scoreboard;
import judgels.api.contest.scoreboard.ScoreboardEntry;
import judgels.api.profile.Profile;
import judgels.api.user.rating.RatingEvent;
import judgels.api.user.rating.UserRating;
import judgels.api.user.rating.UserRatingEvent;
import judgels.contest.ContestRoleChecker;
import judgels.contest.ContestStore;
import judgels.contest.scoreboard.ContestScoreboardBuilder;
import judgels.contest.scoreboard.ContestScoreboardStore;
import judgels.contest.scoreboard.RawContestScoreboard;
import judgels.contrib.api.contest.rating.ContestRating;
import judgels.contrib.api.contest.rating.ContestRatingChanges;
import judgels.contrib.api.contest.rating.ContestRatingHistoryResponse;
import judgels.contrib.api.contest.rating.ContestsPendingRatingResponse;
import judgels.jophiel.JophielClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/contest-rating")
public class ContestRatingResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestScoreboardStore scoreboardStore;
    @Inject protected ContestScoreboardBuilder scoreboardBuilder;
    @Inject protected ContestRatingProvider ratingProvider;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestRatingResource() {}

    @GET
    @Path("/pending")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsPendingRatingResponse getContestsPendingRating(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestRoleChecker.canAdminister(actorJid));

        Optional<RatingEvent> latestEvent = jophielClient.getLatestRatingEvent();
        Instant latestTime = latestEvent.isPresent() ? latestEvent.get().getTime() : Instant.EPOCH;

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
        String userJid = checkFound(jophielClient.translateUsernameToJid(username));

        List<UserRatingEvent> userRatingEvents = jophielClient.getUserRatingEvents(userJid);

        var contestJids = Lists.transform(userRatingEvents, UserRatingEvent::getEventJid);
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

        Map<String, Profile> profilesMap = jophielClient.getProfiles(ranksMap.keySet(), contest.getBeginTime());

        List<String> contestantJids = Lists.newArrayList();
        Map<String, UserRating> currentRatingsMap = new HashMap<>();

        for (Map.Entry<String, Profile> entry : profilesMap.entrySet()) {
            String contestantJid = entry.getKey();
            Optional<UserRating> rating = entry.getValue().getRating();

            contestantJids.add(contestantJid);
            if (rating.isPresent()) {
                currentRatingsMap.put(contestantJid, rating.get());
            }
        }

        Map<String, UserRating> ratingsMap = ratingProvider.getUpdatedRatings(contestantJids, ranksMap, currentRatingsMap);

        return new ContestRatingChanges.Builder()
                .ratingsMap(ratingsMap)
                .profilesMap(profilesMap)
                .build();
    }
}
