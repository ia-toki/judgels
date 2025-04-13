package judgels.jophiel.profile;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.time.Clock;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;

@Path("/api/v2/profiles")
public class ProfileResource {
    @Inject protected Clock clock;
    @Inject protected ProfileStore profileStore;

    @Inject public ProfileResource() {}

    @GET
    @Path("/top")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Page<Profile> getTopRatedProfiles(
            @QueryParam("page") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("50") int pageSize) {

        return profileStore.getTopRatedProfiles(clock.instant(), pageNumber, pageSize);
    }

    @GET
    @Path("/{userJid}/basic")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public BasicProfile getBasicProfile(@PathParam("userJid") String userJid) {
        return checkFound(profileStore.getBasicProfile(clock.instant(), userJid));
    }
}
