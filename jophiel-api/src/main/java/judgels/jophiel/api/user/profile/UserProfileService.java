package judgels.jophiel.api.user.profile;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/{userJid}/profile")
public interface UserProfileService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    UserProfile getProfile(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("userJid") String userJid);

    @GET
    @Path("/public")
    @Produces(APPLICATION_JSON)
    PublicUserProfile getPublicProfile(@PathParam("userJid") String userJid);

    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    UserProfile updateProfile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid,
            UserProfile userProfile);
}
