package judgels.jerahmeel.stats;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Map;
import judgels.jerahmeel.api.stats.UserStats;
import judgels.jerahmeel.api.stats.UserTopStatsEntry;
import judgels.jerahmeel.api.stats.UserTopStatsResponse;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;

@Path("/api/v2/stats/users")
public class UserStatsResource {
    @Inject protected StatsStore statsStore;
    @Inject protected JophielClient jophielClient;

    @Inject public UserStatsResource() {}

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserTopStatsResponse getTopUserStats(
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("50") int pageSize) {

        Page<UserTopStatsEntry> stats = statsStore.getTopUserStats(pageNumber, pageSize);

        var userJids = Lists.transform(stats.getPage(), UserTopStatsEntry::getUserJid);
        Map<String, Profile> profileMap = jophielClient.getProfiles(userJids);

        return new UserTopStatsResponse.Builder()
                .data(stats)
                .profilesMap(profileMap)
                .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserStats getUserStats(@QueryParam("username") String username) {
        String userJid = checkFound(jophielClient.translateUsernameToJid(username));
        return statsStore.getUserStats(userJid);
    }
}
