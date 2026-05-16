package judgels.contest.history;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestInfo;
import judgels.api.contest.history.ContestHistoryEvent;
import judgels.api.contest.history.ContestHistoryResponse;
import judgels.api.user.rating.UserRating;
import judgels.api.user.rating.UserRatingEvent;
import judgels.contest.ContestStore;
import judgels.contest.contestant.ContestContestantStore;
import judgels.jophiel.JophielClient;

@Path("/api/v2/contest-history")
public class ContestHistoryResource {
    @Inject protected ContestStore contestStore;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestHistoryResource() {}

    @GET
    @Path("/public")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestHistoryResponse getPublicHistory(@QueryParam("username") String username) {
        String userJid = checkFound(jophielClient.translateUsernameToJid(username));

        List<Contest> contests = contestStore.getPubliclyParticipatedContests(userJid);
        Map<String, UserRating> ratingsMap = jophielClient.getUserRatingEvents(userJid)
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
