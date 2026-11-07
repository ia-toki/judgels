package judgels.api.user.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRolesUpsertData.class)
public interface UserRolesUpsertData {
    Set<String> getUsernames();
    Set<UserRole> getRoles();

    class Builder extends ImmutableUserRolesUpsertData.Builder {}
}
