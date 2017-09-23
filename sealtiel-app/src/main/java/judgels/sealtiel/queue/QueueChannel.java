package judgels.sealtiel.queue;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public interface QueueChannel extends AutoCloseable {
    void declareQueue(String queueName) throws IOException;

    void pushMessage(String queueName, String message) throws IOException;

    Optional<QueueMessage> popMessage(String queueName) throws IOException;

    void ackMessage(long messageId) throws IOException;

    @Override
    void close() throws IOException, TimeoutException;
}
