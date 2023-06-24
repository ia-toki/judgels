package judgels.jerahmeel.stats;

import static java.util.stream.Collectors.toSet;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jerahmeel.api.stats.UserStats;
import judgels.jerahmeel.api.stats.UserTopStatsEntry;
import judgels.jerahmeel.api.stats.UserTopStatsResponse;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;

@Path("/api/v2/stats/users")
public class UserStatsResource {
    @Inject protected StatsStore statsStore;
    @Inject protected UserClient userClient;

    @Inject public UserStatsResource() {}

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserTopStatsResponse getTopUserStats(
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("50") int pageSize) {

        Page<UserTopStatsEntry> stats = statsStore.getTopUserStats(pageNumber, pageSize);
        Set<String> userJids = stats.getPage().stream().map(UserTopStatsEntry::getUserJid).collect(toSet());
        Map<String, Profile> profileMap = userClient.getProfiles(userJids);

        return new UserTopStatsResponse.Builder()
                .data(stats)
                .profilesMap(profileMap)
                .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserStats getUserStats(@QueryParam("username") String username) {
        String userJid = checkFound(userClient.translateUsernameToJid(username));
        return statsStore.getUserStats(userJid);
    }
}
