package judgels.sealtiel.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import judgels.sealtiel.api.message.Message;
import org.immutables.value.Value;

public class QueueService {
    private final Queue queue;
    private final ObjectMapper objectMapper;

    @Inject
    public QueueService(Queue queue, ObjectMapper objectMapper) {
        this.queue = queue;
        this.objectMapper = objectMapper;
    }

    public Optional<Message> receiveMessage(String clientJid) throws IOException, TimeoutException {
        try (QueueChannel channel = queue.createChannel()) {
            channel.declareQueue(clientJid);

            return channel.popMessage(clientJid).flatMap(queueMessage -> {
                ClientMessage clientMessage;
                try {
                    clientMessage = objectMapper.readValue(queueMessage.getMessage(), ClientMessage.class);
                } catch (IOException e) {
                    return Optional.empty();
                }

                Message message = new Message.Builder()
                        .id(queueMessage.getId())
                        .sourceJid(clientMessage.getSourceJid())
                        .type(clientMessage.getType())
                        .content(clientMessage.getContent())
                        .build();
                return Optional.of(message);
            });
        }
    }

    public void confirmMessage(long messageId) throws IOException, TimeoutException {
        try (QueueChannel channel = queue.createChannel()) {
            channel.ackMessage(messageId);
        }
    }

    public void sendMessage(String sourceJid, String targetJid, String type, String content)
            throws IOException, TimeoutException {

        ClientMessage clientMessage = new ClientMessage.Builder()
                .sourceJid(sourceJid)
                .type(type)
                .content(content)
                .build();

        String message;
        try {
            message = objectMapper.writeValueAsString(clientMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try (QueueChannel channel = queue.createChannel()) {
            channel.declareQueue(targetJid);
            channel.pushMessage(targetJid, message);
        }
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableClientMessage.class)
    interface ClientMessage {
        String getSourceJid();
        String getType();
        String getContent();

        class Builder extends ImmutableClientMessage.Builder {}
    }
}
