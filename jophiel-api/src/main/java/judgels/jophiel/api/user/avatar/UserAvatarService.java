package judgels.jophiel.api.user.avatar;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.IF_MODIFIED_SINCE;

import java.util.Optional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/{userJid}/avatar")
public interface UserAvatarService {
    @GET
    @Path("/")
    Response renderAvatar(
            @HeaderParam(IF_MODIFIED_SINCE) Optional<String> ifModifiedSince,
            @PathParam("userJid") String userJid);

    @DELETE
    @Path("/")
    void deleteAvatar(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, @PathParam("userJid") String userJid);

    @GET
    @Path("/exists")
    boolean avatarExists(@PathParam("userJid") String userJid);

//    This endpoint is not supported for interface-based Java clients
//    because JAX-RS does not support multipart form data.
//
//    @POST
//    @Path("/{userJid}/avatar")
//    @Consumes(MULTIPART_FORM_DATA)
//    void updateUserAvatar(@PathParam("userJid") String userJid, @FormDataParam("file") InputStream fileStream);
}
