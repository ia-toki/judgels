package judgels.jophiel.api.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRole.class)
public interface UserRole {
    Optional<String> getJophiel();
    Optional<String> getSandalphon();
    Optional<String> getUriel();
    Optional<String> getJerahmeel();

    class Builder extends ImmutableUserRole.Builder {}
}
