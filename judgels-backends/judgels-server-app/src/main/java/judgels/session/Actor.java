package judgels.session;

import java.util.Set;
import judgels.api.user.role.UserRole;
import org.immutables.value.Value;

@Value.Immutable
public interface Actor {
    String getUserJid();
    String getUsername();
    Set<UserRole> getRoles();
    String getAvatarUrl();

    class Builder extends ImmutableActor.Builder {}
}
