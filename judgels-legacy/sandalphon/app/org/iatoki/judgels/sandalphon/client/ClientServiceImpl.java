package org.iatoki.judgels.sandalphon.client;

import judgels.service.api.client.Client;

import java.util.Set;

public final class ClientServiceImpl implements ClientService {
    private final Set<Client> clients;

    public ClientServiceImpl(Set<Client> clients) {
        this.clients = clients;
    }

    @Override
    public boolean clientExistsByJid(String clientJid) {
        return findClientByJid(clientJid) != null;
    }

    @Override
    public Client findClientByJid(String clientJid) {
        for (Client client : clients) {
            if (client.getJid().equals(clientJid)) {
                return client;
            }
        }
        return null;
    }
}
