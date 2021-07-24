package judgels.messaging.api;

import org.immutables.value.Value;

@Value.Immutable
public interface Message {
    long getId();
    String getSourceQueueName();
    String getType();
    String getContent();

    class Builder extends ImmutableMessage.Builder {}
}
