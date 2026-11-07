package judgels.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserWithRoles.class)
public interface UserWithRoles {
    String getUserJid();
    Set<UserRole> getRoles();

    class Builder extends ImmutableUserWithRoles.Builder {}
}
