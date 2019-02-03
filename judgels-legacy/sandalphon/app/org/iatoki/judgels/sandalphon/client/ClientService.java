package org.iatoki.judgels.sandalphon.client;

import com.google.inject.ImplementedBy;
import judgels.service.api.client.Client;

@ImplementedBy(ClientServiceImpl.class)
public interface ClientService {

    boolean clientExistsByJid(String clientJid);

    Client findClientByJid(String clientJid);
}
