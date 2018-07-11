package judgels.service.client;

import java.util.Set;
import javax.ws.rs.ForbiddenException;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;

public class ClientChecker {
    private final Set<Client> clients;

    public ClientChecker(Set<Client> clients) {
        this.clients = clients;
    }

    public Client check(BasicAuthHeader authHeader) {
        Client client = authHeader.getClient();
        if (!clients.contains(client)) {
            throw new ForbiddenException();
        }
        return client;
    }
}
