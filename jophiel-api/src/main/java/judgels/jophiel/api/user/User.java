package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
    String getJid();
    String getUsername();
    String getEmail();
    Optional<String> getAvatarUrl();

    class Builder extends ImmutableUser.Builder {}
}
