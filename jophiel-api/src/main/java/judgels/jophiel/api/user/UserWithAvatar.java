package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserWithAvatar.class)
public interface UserWithAvatar {
    String getJid();
    String getUsername();
    Optional<String> getAvatarUrl();

    class Builder extends ImmutableUserWithAvatar.Builder {}
}
