package judgels.michael.actor;

import org.immutables.value.Value;

@Value.Immutable
public interface Actor {
    String getUserJid();
    String getUsername();
    String getAvatarUrl();

    class Builder extends ImmutableActor.Builder {}
}
