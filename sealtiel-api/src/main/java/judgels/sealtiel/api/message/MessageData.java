package judgels.sealtiel.api.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMessageData.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface MessageData {
    String getTargetJid();
    String getType();
    String getContent();

    class Builder extends ImmutableMessageData.Builder {}
}
