package org.iatoki.judgels.api.sealtiel.impls;

import com.palantir.remoting.api.errors.RemoteException;
import com.palantir.remoting3.clients.UserAgent;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.impls.AbstractJudgelsClientAPIImpl;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.api.sealtiel.SealtielMessage;

public final class SealtielClientAPIImpl extends AbstractJudgelsClientAPIImpl implements SealtielClientAPI {
    private final BasicAuthHeader authHeader;
    private final MessageService messageService;

    public SealtielClientAPIImpl(String baseUrl, String clientJid, String clientSecret) {
        super(baseUrl, clientJid, clientSecret);

        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("judgels", UserAgent.Agent.DEFAULT_VERSION));
        messageService = JaxRsClients.create(MessageService.class, baseUrl, userAgent);
        authHeader = BasicAuthHeader.of(Client.of(clientJid, clientSecret));
    }

    @Override
    public SealtielMessage fetchMessage() {
        try {
            return messageService.receiveMessage(authHeader)
                    .map(m -> new SealtielMessage(m.getId(), m.getSourceJid(), "", m.getType(), m.getContent(), ""))
                    .orElse(null);
        } catch (RemoteException e) {
            throw new JudgelsAPIClientException(null, e);
        }
    }

    @Override
    public void acknowledgeMessage(long messageId) {
        try {
            messageService.confirmMessage(authHeader, messageId);
        } catch (RemoteException e) {
            throw new JudgelsAPIClientException(null, e);
        }
    }

    @Override
    public void extendMessageTimeout(long messageId) {}

    @Override
    public void sendMessage(String targetClientJid, String messageType, String message) {
        MessageData data = new MessageData.Builder()
                .targetJid(targetClientJid)
                .type(messageType)
                .content(message)
                .build();

        try {
            messageService.sendMessage(authHeader, data);
        } catch (RemoteException e) {
            throw new JudgelsAPIClientException(null, e);
        }
    }

    @Override
    public void sendLowPriorityMessage(String targetClientJid, String messageType, String message) {
        sendMessage(targetClientJid, messageType, message);
    }
}
