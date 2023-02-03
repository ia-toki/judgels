package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserData.class)
public interface UserData {
    String getUsername();
    Optional<String> getPassword();
    String getEmail();

    class Builder extends ImmutableUserData.Builder {}
}
