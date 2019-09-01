package judgels.jophiel.api.profile;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.Page;

@Path("/api/v2/profiles")
public interface ProfileService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Map<String, Profile> getProfiles(Set<String> userJids, @QueryParam("beforeTime") Optional<Long> time);

    default Map<String, Profile> getProfiles(Set<String> userJids) {
        return getProfiles(userJids, Optional.empty());
    }

    default Map<String, Profile> getProfiles(Set<String> userJids, Instant time) {
        return getProfiles(userJids, Optional.of(time.toEpochMilli()));
    }

    default Profile getProfile(String userJid) {
        Set<String> userJids = new HashSet<>();
        userJids.add(userJid);
        return getProfiles(userJids, Optional.empty()).get(userJid);
    }

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    Page<Profile> getTopRatedProfiles(
            @QueryParam("page") Optional<Integer> page,
            @QueryParam("pageSize") Optional<Integer> pageSize);

    @GET
    @Path("/{userJid}/basic")
    @Produces(APPLICATION_JSON)
    BasicProfile getBasicProfile(@PathParam("userJid") String userJid);
}
