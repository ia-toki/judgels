package judgels.jophiel.api.user.avatar;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/{userJid}/avatar")
public interface UserAvatarService {
    @DELETE
    void deleteAvatar(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("userJid") String userJid);

    @GET
    @Path("/exists")
    @Produces(APPLICATION_JSON)
    boolean avatarExists(@PathParam("userJid") String userJid);

//    These endpoints are not representable as JAX-RS methods

//    @GET
//    @Path("/")
//    Response renderAvatar(
//            @HeaderParam(IF_MODIFIED_SINCE) Optional<String> ifModifiedSince,
//            @PathParam("userJid") String userJid);
//
//    @POST
//    @Path("/{userJid}/avatar")
//    @Consumes(MULTIPART_FORM_DATA)
//    void updateUserAvatar(@PathParam("userJid") String userJid, @FormDataParam("file") InputStream fileStream);
}
