package judgels.jophiel.profile;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
