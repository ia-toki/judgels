package judgels.jophiel.user;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface UserUpdateData {
    String getUsername();
    Optional<String> getPassword();
    Optional<String> getEmail();

    class Builder extends ImmutableUserUpdateData.Builder {}
}
