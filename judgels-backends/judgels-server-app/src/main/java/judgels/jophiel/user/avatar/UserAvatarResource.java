package judgels.jophiel.user.avatar;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static jakarta.ws.rs.core.HttpHeaders.IF_MODIFIED_SINCE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.io.Files;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.jophiel.api.user.User;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.RandomCodeGenerator;
import judgels.service.ServiceUtils;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v2/users/{userJid}/avatar")
public class UserAvatarResource {
    private static final String DEFAULT_AVATAR = "assets/avatar-default.png";

    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject @UserAvatarFs protected FileSystem avatarFs;

    @Inject public UserAvatarResource() {}

    @DELETE
    @UnitOfWork
    public void deleteAvatar(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        userStore.updateUserAvatar(userJid, null);
    }

    @GET
    @Path("/exists")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public boolean avatarExists(@PathParam("userJid") String userJid) {
        return userStore.getUserAvatarFilename(userJid).isPresent();
    }

    @GET
    @UnitOfWork(readOnly = true)
    public Response renderAvatar(
            @HeaderParam(IF_MODIFIED_SINCE) Optional<String> ifModifiedSince,
            @PathParam("userJid") String userJid) {

        Optional<String> avatarFilename = userStore.getUserAvatarFilename(userJid);
        if (avatarFilename.isPresent()) {
            return ServiceUtils.buildMediaResponse(avatarFs.getPublicFileUrl(Paths.get(avatarFilename.get())), ifModifiedSince);
        }
        return ServiceUtils.buildMediaResponse(
                UserAvatarResource.class.getClassLoader().getResourceAsStream(DEFAULT_AVATAR),
                "image/png",
                new Date(1532822400),
                ifModifiedSince);
    }

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void updateUserAvatar(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @HeaderParam(CONTENT_LENGTH) int contentLength,
            @PathParam("userJid") String userJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        User user = checkFound(userStore.getUserByJid(userJid));

        String extension = Files.getFileExtension(fileDetails.getFileName());
        String destFilename = user.getJid() + "-" + RandomCodeGenerator.newCode() + "." + extension;

        avatarFs.uploadPublicFile(Paths.get(destFilename), fileStream);
        userStore.updateUserAvatar(user.getJid(), destFilename);
    }
}
