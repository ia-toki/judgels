package tlx.contest.rating;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import judgels.api.contest.ContestInfo;
import judgels.api.user.rating.UserRatingEvent;
import judgels.contest.ContestStore;
import judgels.user.UserStore;
import judgels.user.rating.UserRatingStore;
import tlx.api.contest.rating.ContestRating;
import tlx.api.contest.rating.ContestRatingHistoryResponse;

@Path("/api/v2/contest-rating")
public class ContestRatingResource {
    @Inject protected ContestStore contestStore;
    @Inject protected UserStore userStore;
    @Inject protected UserRatingStore userRatingStore;

    @Inject public ContestRatingResource() {}

    @GET
    @Path("/history")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestRatingHistoryResponse getRatingHistory(@QueryParam("username") String username) {
        String userJid = checkFound(userStore.translateUsernameToJid(username));

        List<UserRatingEvent> userRatingEvents = userRatingStore.getUserRatingEvents(userJid);

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
}
