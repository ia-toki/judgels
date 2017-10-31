package judgels.sealtiel.message;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import judgels.client.ClientChecker;
import judgels.client.api.Client;
import judgels.client.api.auth.BasicAuthHeader;
import judgels.sealtiel.api.message.Message;
import judgels.sealtiel.api.message.MessageData;
import judgels.sealtiel.api.message.MessageService;
import judgels.sealtiel.queue.QueueService;

public class MessageResource implements MessageService {
    private final ClientChecker clientChecker;
    private final QueueService queueService;

    @Inject
    public MessageResource(ClientChecker clientChecker, QueueService queueService) {
        this.clientChecker = clientChecker;
        this.queueService = queueService;
    }

    @Override
    public Optional<Message> receiveMessage(BasicAuthHeader authHeader) {
        Client client = authHeader.getClient();
        clientChecker.check(client);

        try {
            return queueService.receiveMessage(client.getJid());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void confirmMessage(BasicAuthHeader authHeader, long messageId) {
        Client client = authHeader.getClient();
        clientChecker.check(client);

        try {
            queueService.confirmMessage(messageId);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(BasicAuthHeader authHeader, MessageData messageData) {
        Client client = authHeader.getClient();
        clientChecker.check(client);

        try {
            queueService.sendMessage(
                    client.getJid(),
                    messageData.getTargetJid(),
                    messageData.getType(),
                    messageData.getContent());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
