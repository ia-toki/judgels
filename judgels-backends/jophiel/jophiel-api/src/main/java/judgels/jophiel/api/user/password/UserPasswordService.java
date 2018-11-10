package judgels.jophiel.api.user.password;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/user-password")
public interface UserPasswordService {
    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    PasswordsUpdateResponse updateUserPasswords(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            Map<String, String> usernameToPasswordMap);
}
