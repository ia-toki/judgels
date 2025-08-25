package judgels.messaging.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMessage.class)
public interface Message {
    String getSourceQueueName();
    String getType();
    String getContent();

    class Builder extends ImmutableMessage.Builder {}
}
