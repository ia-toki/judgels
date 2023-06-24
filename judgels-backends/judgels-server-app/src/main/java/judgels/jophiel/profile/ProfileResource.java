package judgels.jophiel.profile;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;

@Path("/api/v2/profiles")
public class ProfileResource {
    @Inject protected Clock clock;
    @Inject protected ProfileStore profileStore;

    @Inject public ProfileResource() {}

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Map<String, Profile> getProfiles(Set<String> userJids, @QueryParam("beforeTime") Optional<Long> time) {
        return profileStore.getProfiles(userJids, time.map(Instant::ofEpochMilli).orElse(clock.instant()));
    }

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Page<Profile> getTopRatedProfiles(
            @QueryParam("page") Optional<Integer> pageNumber,
            @QueryParam("pageSize") Optional<Integer> pageSize) {

        return profileStore.getTopRatedProfiles(clock.instant(), pageNumber.orElse(1), pageSize.orElse(50));
    }

    @GET
    @Path("/{userJid}/basic")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public BasicProfile getBasicProfile(@PathParam("userJid") String userJid) {
        return checkFound(profileStore.getBasicProfile(clock.instant(), userJid));
    }
}
