package judgels.jophiel.user.avatar;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import judgels.fs.FileSystem;
import judgels.jophiel.api.user.User;
import judgels.jophiel.role.RoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.RandomCodeGenerator;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v2/users")
public class UserAvatarResource {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final UserStore userStore;
    private final FileSystem avatarFs;

    @Inject
    public UserAvatarResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            UserStore userStore,
            @UserAvatarFs FileSystem avatarFs) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.avatarFs = avatarFs;
        this.userStore = userStore;
    }

    @POST
    @Path("/{userJid}/avatar")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void updateUserAvatar(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @HeaderParam(CONTENT_LENGTH) int contentLength,
            @PathParam("userJid") String userJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canUpdateUser(actorJid, userJid));

        User user = checkFound(userStore.findUserByJid(userJid));

        String extension = Files.getFileExtension(fileDetails.getFileName());
        String destFilename = user.getJid() + "-" + RandomCodeGenerator.newCode() + "." + extension;

        avatarFs.uploadPublicFile(fileStream, ImmutableList.of(), destFilename);
        userStore.updateUserAvatar(user.getJid(), destFilename);
    }
}
