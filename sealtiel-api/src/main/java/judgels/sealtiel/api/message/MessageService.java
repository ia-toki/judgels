package judgels.sealtiel.api.message;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.client.api.auth.BasicAuthHeader;

@Path("/api/v2/messages")
public interface MessageService {
    @POST
    @Path("/retrieve")
    @Produces(APPLICATION_JSON)
    Optional<Message> receiveMessage(@HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader);

    @POST
    @Path("/{messageId}/confirm")
    void confirmMessage(@HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader, @PathParam("messageId") long messageId);

    @POST
    @Path("/send")
    @Consumes(APPLICATION_JSON)
    void sendMessage(@HeaderParam(AUTHORIZATION) BasicAuthHeader authHeader, MessageData request);
}
