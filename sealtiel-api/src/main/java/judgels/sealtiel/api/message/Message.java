package judgels.sealtiel.api.message;

import org.immutables.value.Value;

@Value.Immutable
public interface Message {
    long getId();
    String getSourceJid();
    String getType();
    String getContent();

    class Builder extends ImmutableMessage.Builder {}
}
