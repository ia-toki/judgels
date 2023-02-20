package judgels.jophiel.api.actor;

import judgels.jophiel.api.role.UserRole;
import org.immutables.value.Value;

@Value.Immutable
public interface Actor {
    String getUserJid();
    String getUsername();
    UserRole getRole();
    String getAvatarUrl();

    class Builder extends ImmutableActor.Builder {}
}
