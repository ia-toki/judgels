package judgels.jophiel.user.avatar;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.IF_MODIFIED_SINCE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.buildImageResponse;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.io.Files;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.fs.FileSystem;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.avatar.UserAvatarService;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.RandomCodeGenerator;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class UserAvatarResource implements UserAvatarService {
    private static final String DEFAULT_AVATAR = "assets/avatar-default.png";

    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;
    private final FileSystem avatarFs;

    @Inject
    public UserAvatarResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            @UserAvatarFs FileSystem avatarFs) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.avatarFs = avatarFs;
        this.userStore = userStore;
    }

    @Override
    @UnitOfWork
    public void deleteAvatar(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        checkFound(userStore.updateUserAvatar(userJid, null));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public boolean avatarExists(String userJid) {
        return userStore.getUserAvatarUrl(userJid).isPresent();
    }

    @GET
    @UnitOfWork(readOnly = true)
    public Response renderAvatar(
            @HeaderParam(IF_MODIFIED_SINCE) Optional<String> ifModifiedSince,
            @PathParam("userJid") String userJid) {

        Optional<String> avatarUrl = userStore.getUserAvatarUrl(userJid);
        if (avatarUrl.isPresent()) {
            return buildImageResponse(avatarUrl.get(), ifModifiedSince);
        }
        return buildImageResponse(
                UserAvatarResource.class.getClassLoader().getResourceAsStream(DEFAULT_AVATAR),
                "png",
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
