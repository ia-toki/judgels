package org.iatoki.judgels.sandalphon.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class ClientServiceImpl implements ClientService {

    private final ClientDao clientDao;

    @Inject
    public ClientServiceImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public boolean clientExistsByJid(String clientJid) {
        return clientDao.existsByJid(clientJid);
    }

    @Override
    public Client findClientById(long clientId) throws ClientNotFoundException {
        ClientModel clientModel = clientDao.findById(clientId);
        if (clientModel == null) {
            throw new ClientNotFoundException("Client not found.");
        }

        return createClientFromModel(clientModel);
    }


    @Override
    public Client findClientByJid(String clientJid) {
        ClientModel clientModel = clientDao.findByJid(clientJid);

        return createClientFromModel(clientModel);
    }

    @Override
    public Client createClient(String name, String userJid, String userIpAddress) {
        ClientModel clientModel = new ClientModel();
        clientModel.name = name;
        clientModel.secret = JudgelsPlayUtils.generateNewSecret();

        clientDao.persist(clientModel, userJid, userIpAddress);

        return createClientFromModel(clientModel);
    }

    @Override
    public void updateClient(String clientJid, String name, String userJid, String userIpAddress) {
        ClientModel clientModel = clientDao.findByJid(clientJid);
        clientModel.name = name;

        clientDao.edit(clientModel, userJid, userIpAddress);
    }

    @Override
    public Page<Client> getPageOfClients(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = clientDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<ClientModel> clientModels = clientDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Client> clients = Lists.transform(clientModels, m -> createClientFromModel(m));

        return new Page<>(clients, totalPages, pageIndex, pageSize);
    }

    private static Client createClientFromModel(ClientModel clientModel) {
        return new Client(clientModel.id, clientModel.jid, clientModel.name, clientModel.secret);
    }
}
