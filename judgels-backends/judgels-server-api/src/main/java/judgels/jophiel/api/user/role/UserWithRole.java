package judgels.jophiel.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserWithRole.class)
public interface UserWithRole {
    String getUserJid();
    UserRole getRole();

    class Builder extends ImmutableUserWithRole.Builder {}
}
