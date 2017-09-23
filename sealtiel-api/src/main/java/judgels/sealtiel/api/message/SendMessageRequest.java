package judgels.sealtiel.api.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSendMessageRequest.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface SendMessageRequest {
    @JsonProperty("targetClientJid")
    String getTargetJid();

    @JsonProperty("messageType")
    String getType();

    @JsonProperty("message")
    String getContent();

    class Builder extends ImmutableSendMessageRequest.Builder {}
}
