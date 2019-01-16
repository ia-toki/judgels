package org.iatoki.judgels.sandalphon.client;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.api.JudgelsAppClientService;

@ImplementedBy(ClientServiceImpl.class)
public interface ClientService extends JudgelsAppClientService {

    boolean clientExistsByJid(String clientJid);

    Client findClientById(long clientId) throws ClientNotFoundException;

    Client findClientByJid(String clientJid);

    Client createClient(String name, String userJid, String userIpAddress);

    void updateClient(String clientJid, String name, String userJid, String userIpAddress);

    Page<Client> getPageOfClients(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);
}
