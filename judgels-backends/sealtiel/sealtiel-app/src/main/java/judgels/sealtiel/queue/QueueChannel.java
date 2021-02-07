package judgels.sealtiel.queue;

import java.io.IOException;
import java.util.Optional;

public interface QueueChannel {
    void declareQueue(String queueName) throws IOException;

    void pushMessage(String queueName, String message) throws IOException;

    Optional<QueueMessage> popMessage(String queueName) throws IOException;

    void ackMessage(long messageId) throws IOException;

    void rejectMessage(long messageId) throws IOException;
}
