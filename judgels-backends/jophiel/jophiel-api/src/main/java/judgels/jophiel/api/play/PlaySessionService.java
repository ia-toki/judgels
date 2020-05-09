package judgels.jophiel.api.play;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.session.Credentials;

@Path("/api/play/session")
public interface PlaySessionService {
    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    PlaySession logIn(Credentials credentials);
}
