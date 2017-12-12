package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
    String getJid();
    String getUsername();
    String getEmail();

    class Builder extends ImmutableUser.Builder {}
}
