package judgels.training.stats;

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
import judgels.api.profile.Profile;
import judgels.api.training.stats.UserStats;
import judgels.api.training.stats.UserTopStatsEntry;
import judgels.api.training.stats.UserTopStatsResponse;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.user.UserStore;

@Path("/api/v4/training/stats/users")
public class TrainingUserStatsResource {
    @Inject protected StatsStore statsStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected UserStore userStore;

    @Inject public TrainingUserStatsResource() {}

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserTopStatsResponse getTopUserStats(
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("50") int pageSize) {

        Page<UserTopStatsEntry> stats = statsStore.getTopUserStats(pageNumber, pageSize);

        var userJids = Lists.transform(stats.getPage(), UserTopStatsEntry::getUserJid);
        Map<String, Profile> profileMap = profileStore.getProfiles(userJids);

        return new UserTopStatsResponse.Builder()
                .data(stats)
                .profilesMap(profileMap)
                .build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserStats getUserStats(@QueryParam("username") String username) {
        String userJid = checkFound(userStore.translateUsernameToJid(username));
        return statsStore.getUserStats(userJid);
    }
}
