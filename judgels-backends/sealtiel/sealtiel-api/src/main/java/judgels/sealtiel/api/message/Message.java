package judgels.sealtiel.api.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMessage.class)
public interface Message {
    long getId();
    String getSourceJid();
    String getType();
    String getContent();

    class Builder extends ImmutableMessage.Builder {}
}
