package judgels.service.client;

import java.util.Set;
import javax.annotation.Nullable;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import org.eclipse.jetty.server.Response;

public class ClientChecker {
    private final Set<Client> clients;

    public ClientChecker(Set<Client> clients) {
        this.clients = clients;
    }

    public Client check(@Nullable BasicAuthHeader authHeader) {
        if (authHeader == null) {
            throw new NotAuthorizedException(Response.SC_UNAUTHORIZED);
        }

        Client client = authHeader.getClient();
        if (!clients.contains(client)) {
            throw new ForbiddenException();
        }
        return client;
    }
}
