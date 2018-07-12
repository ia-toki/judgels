package judgels.sealtiel.queue;

import org.immutables.value.Value;

@Value.Immutable
public abstract class QueueMessage {
    public abstract long getId();
    public abstract String getMessage();

    public static QueueMessage of(long id, String message) {
        return ImmutableQueueMessage.builder()
                .id(id)
                .message(message)
                .build();
    }
}
